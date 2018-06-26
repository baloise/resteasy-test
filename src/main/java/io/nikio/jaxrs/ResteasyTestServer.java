package io.nikio.jaxrs;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResteasyTestServer {

    private static final Logger logger = Logger.getLogger(ResteasyTestServer.class.getName());

    public static void main(String[] args) {
        UndertowJaxrsServer ut = new UndertowJaxrsServer();
        Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(8080, "localhost");
        ut.start(serverBuilder);

        ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
//        resteasyDeployment.setApplicationClass(TestApplication.class.getName());
        resteasyDeployment.setResources(Stream.of(new RestEndpoint()).collect(Collectors.toList()));
        resteasyDeployment.setProviders(Stream.of(new ObjectMapperContextResolver()).collect(Collectors.toList()));

        DeploymentInfo deploymentInfo = ut.undertowDeployment(resteasyDeployment);
        deploymentInfo.setClassLoader(ResteasyTestServer.class.getClassLoader());
        deploymentInfo.setDeploymentName("appl-deployment");
        deploymentInfo.setContextPath("/appl");

        ut.deploy(deploymentInfo);
        logger.info("Undertow with JAX-RS is running");
    }
}
