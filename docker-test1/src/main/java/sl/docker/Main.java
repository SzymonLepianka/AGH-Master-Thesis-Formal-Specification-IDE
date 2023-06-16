package sl.docker;

import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.*;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        DefaultDockerClientConfig config =
                DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("tcp://localhost:2375").build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

        // Build an image using the Dockerfile
        String dockerfilePath = "../docker/Dockerfile";
        String imageTagName = "docker-image-for-provers";
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

        try {
            System.out.println("Start of execution of the example command");
            ExecuteExampleCommand.executeExampleCommand(containerName, dockerClient);
            System.out.println("Execution of the example command is finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Start of the prover9 command");
            ExecuteProver9Command.executeProver9Command(containerName, dockerClient);
            System.out.println("End of prover9 command execution");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}