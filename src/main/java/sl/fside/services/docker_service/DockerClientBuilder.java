package sl.fside.services.docker_service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.*;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

public class DockerClientBuilder {
    private final DockerClientConfig dockerClientConfig;
    private DockerCmdExecFactory dockerCmdExecFactory = null;
    private DockerHttpClient dockerHttpClient = null;

    private DockerClientBuilder(DockerClientConfig dockerClientConfig) {
        this.dockerClientConfig = dockerClientConfig;
    }

    public static DockerClientBuilder getInstance() {
        return new DockerClientBuilder(DefaultDockerClientConfig.createDefaultConfigBuilder().build());
    }

    /** @deprecated */
    @Deprecated
    public static DockerClientBuilder getInstance(DefaultDockerClientConfig.Builder dockerClientConfigBuilder) {
        return getInstance((DockerClientConfig)dockerClientConfigBuilder.build());
    }

    public static DockerClientBuilder getInstance(DockerClientConfig dockerClientConfig) {
        return new DockerClientBuilder(dockerClientConfig);
    }

    /** @deprecated */
    @Deprecated
    public static DockerClientBuilder getInstance(String serverUrl) {
        return new DockerClientBuilder(DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(serverUrl).build());
    }

    /** @deprecated */
    @Deprecated
    public static DockerCmdExecFactory getDefaultDockerCmdExecFactory() {
        return new JerseyDockerCmdExecFactory();
    }

    /** @deprecated */
    @Deprecated
    public DockerClientBuilder withDockerCmdExecFactory(DockerCmdExecFactory dockerCmdExecFactory) {
        this.dockerCmdExecFactory = dockerCmdExecFactory;
        this.dockerHttpClient = null;
        return this;
    }

    public DockerClientBuilder withDockerHttpClient(DockerHttpClient dockerHttpClient) {
        this.dockerCmdExecFactory = null;
        this.dockerHttpClient = dockerHttpClient;
        return this;
    }

    public DockerClient build() {
        if (this.dockerHttpClient != null) {
            return DockerClientImpl.getInstance(this.dockerClientConfig, this.dockerHttpClient);
        } else if (this.dockerCmdExecFactory != null) {
            return DockerClientImpl.getInstance(this.dockerClientConfig).withDockerCmdExecFactory(this.dockerCmdExecFactory);
        } else {
//            Logger log = LoggerFactory.getLogger(DockerClientBuilder.class);
            System.out.println("'dockerHttpClient' should be set.Falling back to Jersey, will be an error in future releases.");
            return DockerClientImpl.getInstance(this.dockerClientConfig, (new JerseyDockerHttpClient.Builder()).dockerHost(this.dockerClientConfig.getDockerHost()).sslConfig(this.dockerClientConfig.getSSLConfig()).build());
        }
    }
}
