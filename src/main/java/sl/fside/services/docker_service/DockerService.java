package sl.fside.services.docker_service;

import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.*;
import com.google.inject.*;
import sl.fside.factories.*;
import sl.fside.services.*;

import java.io.*;
import java.util.*;

public class DockerService {

    private final IModelFactory modelFactory;
    private final LoggerService loggerService;

    @Inject
    DockerService(IModelFactory modelFactory, LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {
        var config =
                DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("tcp://localhost:2375").build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

        // Build an image using the Dockerfile
        String dockerfilePath = "docker/Dockerfile";
        String imageTagName = "docker-image-prover9";
        String imageBuildResponse = dockerClient.buildImageCmd().withDockerfile(new File(dockerfilePath))
                .withTags(Collections.singleton(imageTagName)).exec(new BuildImageResultCallback()).awaitImageId();

        System.out.println("Image built: " + imageBuildResponse);

        String containerName = "my-container";

        // Check if the container exists
        List<Container> containers =
                dockerClient.listContainersCmd().withNameFilter(Collections.singleton(containerName))
                        .withStatusFilter(Arrays.asList("running", "exited"))  // Include "exited" status
                        .exec();
        boolean containerExists =
                containers.stream().anyMatch(container -> container.getNames()[0].equals("/" + containerName));

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
    }

    public void executeCommand() {
    }
}
