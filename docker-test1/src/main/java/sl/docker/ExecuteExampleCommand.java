package sl.docker;

import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.core.command.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.utils.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

public class ExecuteExampleCommand {
    public static void executeExampleCommand(String containerName, DockerClient dockerClient) throws Exception {
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
