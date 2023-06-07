package sl.fside.services.docker_service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.google.inject.Inject;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import sl.fside.factories.IModelFactory;
import sl.fside.services.LoggerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DockerService {

    private final IModelFactory modelFactory;
    private final LoggerService loggerService;
    private final String DOCKER_HOST = "tcp://localhost:2375";
    private final String CONTAINER_NAME = "my-container";
    private final String IMAGE_TAG_NAME = "docker-image-prover9";
    private final String DOCKERFILE_PATH = "docker/Dockerfile";
    private DockerClient dockerClient;

    @Inject
    DockerService(IModelFactory modelFactory, LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(DOCKER_HOST).build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();

        // Build an image using the Dockerfile
        String imageBuildResponse = dockerClient.buildImageCmd().withDockerfile(new File(DOCKERFILE_PATH))
                .withTags(Collections.singleton(IMAGE_TAG_NAME)).exec(new BuildImageResultCallback()).awaitImageId();
        loggerService.logInfo("Image built: " + imageBuildResponse);

        // Check if the container exists
        List<Container> containers =
                dockerClient.listContainersCmd().withNameFilter(Collections.singleton(CONTAINER_NAME))
                        .withStatusFilter(Arrays.asList("running", "exited"))  // Include "exited" status
                        .exec();
        boolean containerExists =
                containers.stream().anyMatch(container -> container.getNames()[0].equals("/" + CONTAINER_NAME));

        // Remove the container if it exists
        if (containerExists) {
            dockerClient.removeContainerCmd(CONTAINER_NAME).withForce(true).exec();
            loggerService.logInfo("Container removed: " + CONTAINER_NAME);
        } else {
            loggerService.logInfo("Container does not exist: " + CONTAINER_NAME);
        }

        // Create a container from the built image
        CreateContainerResponse container =
                dockerClient.createContainerCmd(IMAGE_TAG_NAME).withName(CONTAINER_NAME).exec();
        loggerService.logInfo("Container created: " + container.getId());

        // Start the container
        dockerClient.startContainerCmd(container.getId()).exec();
        loggerService.logInfo("Container started: " + container.getId());

        // Add a shutdown hook to stop the container when the application exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Stop the container
            dockerClient.stopContainerCmd(CONTAINER_NAME).exec();
            loggerService.logInfo("Container stopped: " + CONTAINER_NAME);
        }));
    }

    public void executeProver9Command(Path inputFilePath) throws Exception {
        String command = "/opt/LADR-2009-11A/bin/prover9 -f /shared/" + inputFilePath.getFileName() +
                " > /shared/output_prover9.txt 2> /shared/logs_prover9.txt";
        String proverOutputFilePath = "/shared/output_prover9.txt";
        String proverLogsFilePath = "/shared/logs_prover9.txt";
        String proverName = "Prover9";
        executeCommand(inputFilePath, command, proverOutputFilePath, proverLogsFilePath, proverName);
    }

    public void executeSpassCommand(Path inputFilePath) throws Exception {
        String command = "/opt/SPASS-3.5/SPASS /shared/" + inputFilePath.getFileName() +
                " > /shared/output_spass.txt 2> /shared/logs_spass.txt";
        String proverOutputFilePath = "/shared/output_spass.txt";
        String proverLogsFilePath = "/shared/logs_spass.txt";
        String proverName = "SPASS";
        executeCommand(inputFilePath, command, proverOutputFilePath, proverLogsFilePath, proverName);
    }

    public void executeInkresatCommand(Path inputFilePath) throws Exception {
        String command = "/opt/inkresat-1.0/inkresat /shared/" + inputFilePath.getFileName() +
                " > /shared/output_inkresat.txt 2> /shared/logs_inkresat.txt";
        String proverOutputFilePath = "/shared/output_inkresat.txt";
        String proverLogsFilePath = "/shared/logs_inkresat.txt";
        String proverName = "InKreSAT";
        executeCommand(inputFilePath, command, proverOutputFilePath, proverLogsFilePath, proverName);
    }

    private void executeCommand(Path inputFilePath, String command, String proverOutputFilePath,
                                String proverLogsFilePath, String proverName) throws Exception {
        // copy input file to the container
        dockerClient.copyArchiveToContainerCmd(CONTAINER_NAME).withHostResource(inputFilePath.toString())
                .withRemotePath("/shared").exec();
        loggerService.logInfo(inputFilePath + " copied to container");

        // Create the exec creation request
        ExecCreateCmdResponse execCreateCmdResponse =
                dockerClient.execCreateCmd(CONTAINER_NAME).withCmd("bash", "-c", command).withAttachStdout(true)
                        .withAttachStderr(true).exec();

        // Start the exec command
        ExecStartResultCallback callback = new ExecStartResultCallback();
        dockerClient.execStartCmd(execCreateCmdResponse.getId()).withDetach(false).withTty(true).exec(callback);

        // Wait for the command to complete
        callback.awaitCompletion();

        // Create output folder if it doesn't exist
        File folder = new File("prover_output/");
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                // Handle the case when folder creation fails
                throw new Exception("Failed to create the folder prover_output/");
            }
        }

        // Copy file from container
        try (TarArchiveInputStream tarStream = new TarArchiveInputStream(
                dockerClient.copyArchiveFromContainerCmd(CONTAINER_NAME, proverOutputFilePath).exec())) {
            String outputFilePath = inputFilePath.toString().replace("input", "output");
            unTar(tarStream, new File(outputFilePath));
            loggerService.logInfo(proverName + " output file saved: " + outputFilePath);
        }

        // Create logs folder if it doesn't exist
        File proverLogsFolder = new File("prover_logs/");
        if (!proverLogsFolder.exists()) {
            boolean created = proverLogsFolder.mkdirs();
            if (!created) {
                // Handle the case when folder creation fails
                throw new Exception("Failed to create the folder prover_logs/");
            }
        }

        // Copy logs file from container
        try (TarArchiveInputStream tarStream = new TarArchiveInputStream(
                dockerClient.copyArchiveFromContainerCmd(CONTAINER_NAME, proverLogsFilePath).exec())) {
            String logsFilePath = inputFilePath.toString().replace("input", "logs");
            unTar(tarStream, new File(logsFilePath));
            loggerService.logInfo(proverName + " logs file saved: " + logsFilePath);
        }
    }

    public void unTar(TarArchiveInputStream tis, File destFile) throws IOException {
        TarArchiveEntry tarEntry;
        while ((tarEntry = tis.getNextTarEntry()) != null) {
            if (tarEntry.isDirectory()) {
                if (!destFile.exists()) {
                    destFile.mkdirs();
                }
            } else {
                FileOutputStream fos = new FileOutputStream(destFile);
                IOUtils.copy(tis, fos);
                fos.close();
            }
        }
        tis.close();
    }
}
