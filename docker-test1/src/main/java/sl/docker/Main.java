package sl.docker;

import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.*;
import com.github.dockerjava.core.command.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.utils.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        DefaultDockerClientConfig config =
                DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("tcp://localhost:2375").build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

        // Build an image using the Dockerfile
        String dockerfilePath = "../docker/Dockerfile";
        String imageTagName = "docker-image-prover9";
        String imageBuildResponse = dockerClient.buildImageCmd().withDockerfile(new File(dockerfilePath))
                .withTags(Collections.singleton(imageTagName)).exec(new BuildImageResultCallback()).awaitImageId();

        System.out.println("Image built: " + imageBuildResponse);

        String containerName = "my-container";

        // Check if the container exists
        boolean containerExists =
                dockerClient.listContainersCmd().withNameFilter(Collections.singleton(containerName)).exec().stream()
                        .anyMatch(container -> container.getNames()[0].equals("/" + containerName));

        // Remove the container if it exists
        if (containerExists) {
            dockerClient.removeContainerCmd(containerName).withForce(true).exec();
            System.out.println("Container removed: " + containerName);
        } else {
            System.out.println("Container does not exist: " + containerName);
        }

        // Create a container from the built image
        CreateContainerResponse container =
                dockerClient.createContainerCmd(imageTagName).withName(containerName).exec();

        System.out.println("Container created: " + container.getId());

        // Start the container
        dockerClient.startContainerCmd(container.getId()).exec();

        System.out.println("Container started: " + container.getId());

        // Add a shutdown hook to stop the container when the application exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Stop the container
            dockerClient.stopContainerCmd(containerName).exec();
            System.out.println("Container stopped: " + containerName);
        }));

        try {
            executeCommand(containerName, dockerClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeCommand(String containerName, DockerClient dockerClient) throws Exception {
        // Create the command you want to execute inside the container
        String command = "echo 'Hello, Docker!' > /shared/output.txt";  // Replace with your desired command

        // Create the exec creation request
        ExecCreateCmdResponse execCreateCmdResponse =
                dockerClient.execCreateCmd(containerName).withCmd("bash", "-c", command).withAttachStdout(true)
                        .withAttachStderr(true).exec();

        // Start the exec command
        ExecStartResultCallback callback = new ExecStartResultCallback();
        dockerClient.execStartCmd(execCreateCmdResponse.getId()).withDetach(false).withTty(true).exec(callback);

        // Wait for the command to complete
        callback.awaitCompletion();

        // Retrieve the output file from the container
        try (InputStream inputStream = dockerClient.copyArchiveFromContainerCmd(containerName, "/shared/output.txt")
                .exec()) {
            // Save the original content to a separate file
            Path originalOutputFilePath = Paths.get("../docker/shared/output_original.tar.gz");
            Files.copy(inputStream, originalOutputFilePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Original output file saved: " + originalOutputFilePath);

            // Reset the InputStream to read the content again
            inputStream.reset();

            TarArchiveInputStream tarInputStream = new TarArchiveInputStream(inputStream);
            TarArchiveEntry entry = tarInputStream.getNextTarEntry();
            if (entry != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(tarInputStream, outputStream);
                String fileContent = outputStream.toString(StandardCharsets.UTF_8).trim();

                // Save the trimmed content to a separate file
                Path outputFilePath = Paths.get("../docker/shared/output_trimmed.txt");
                Files.writeString(outputFilePath, fileContent);
                System.out.println("Trimmed output file saved: " + outputFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}