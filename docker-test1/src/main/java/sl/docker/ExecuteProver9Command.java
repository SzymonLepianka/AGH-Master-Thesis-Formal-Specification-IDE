package sl.docker;

import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.core.command.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.utils.*;

import java.io.*;

public class ExecuteProver9Command {
    public static void executeProver9Command(String containerName, DockerClient dockerClient) throws Exception {
        // Path to the example.in file on the host machine
        String exampleFilePath = "../docker/shared/example.in";

        // Copy the example.in file to the container
        dockerClient.copyArchiveToContainerCmd(containerName).withHostResource(exampleFilePath)
                .withRemotePath("/shared").exec();
        System.out.println("example.in file copied to container");

        // Create the command you want to execute inside the container
        String command = "bin/prover9 -f /shared/example.in > /shared/output_prover9.txt";

        // Create the exec creation request
        ExecCreateCmdResponse execCreateCmdResponse =
                dockerClient.execCreateCmd(containerName).withCmd("bash", "-c", command).withAttachStdout(true)
                        .withAttachStderr(true).exec();

        // Start the exec command
        ExecStartResultCallback callback = new ExecStartResultCallback();
        dockerClient.execStartCmd(execCreateCmdResponse.getId()).withDetach(false).withTty(true).exec(callback);

        // Wait for the command to complete
        callback.awaitCompletion();

        // Copy file from container
        try (TarArchiveInputStream tarStream = new TarArchiveInputStream(
                dockerClient.copyArchiveFromContainerCmd(containerName, "/shared/output_prover9.txt").exec())) {
            String proverOutputFilePath = "../docker/shared/output_prover9.txt";
            unTar(tarStream, new File(proverOutputFilePath));
            System.out.println("Prover9 output file saved: " + proverOutputFilePath);
        }
    }

    public static void unTar(TarArchiveInputStream tis, File destFile) throws IOException {
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
