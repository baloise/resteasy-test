package io.nikio.jaxrs;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * ResteasyTest provides a convenient way to test JAX-RS resource in your JUnit test. It allows you to define you're
 * REST endpoints in your tests classes and verify that the exposed resources work properly. ResteasyTest is similar to
 * JerseyTest but uses Resteasy as provider and is not as feature rich. The dependencies used are the same as in
 * JBoss EAP / WildFly. These are undertow as ServletContainer, Resteasy as JAX-RS implementation and jackson 2 for
 * JSON support.
 */
public class ResteasyTest {

    private static final Logger logger = Logger.getLogger(ResteasyTest.class.getName());

    private Client testClient;
    private UndertowJaxrsServer testContainer;
    private int bindPort;
    private String bindUrl = "localhost";

    public ResteasyTest() {
    }

    /**
     * Finds a free server port.
     *
     * @return port number.
     * @throws IOException
     */
    public static int findFreePort() throws IOException {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }

    /**
     * Sets up a new JAX-RS server test instance for every JUnit test.
     *
     * @throws IOException
     */
    @Before
    public void restEasyTestSetUp() throws IOException {
        this.bindPort = findFreePort();
        logger.info("Staring test container at " + getBaseUri());
        this.testContainer = createTestContainer(bindPort, bindUrl);
        ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder();
        configureProvider().forEach(resteasyClientBuilder::register);
        this.testClient = resteasyClientBuilder.build();
    }

    /**
     * Closes test container and test client after every JUnit test.
     */
    @After
    public void restEasyTestTearDown() {
        if (testClient != null) {
            testClient.close();
            testClient = null;
        }
        if (testContainer != null) {
            testContainer.stop();
            testContainer = null;
        }
    }

    /**
     * List of JAX-RS resources to test. Must return actual instances of resource.
     * Method need to be overridden.
     *
     * @return
     */
    public List<Object> configureResources() {
        return new ArrayList<>();
    }

    /**
     * List of JAX-RS {@link javax.ws.rs.ext.Provider} to use while testing. Must return actual instances of provider.
     * Method need to be overridden
     * @return
     */
    public List<Object> configureProvider() {
        return new ArrayList<>();
    }

    /**
     * Define {@link Application} to use while testing
     * @return
     */
    public Application configureApplication() {
        return null;
    }

    /**
     * Returns the URL with port where the REST endpoints are exposed.
     * @return
     */
    public String getBaseUri() {
        return "http://" + bindUrl + ":" + bindPort;
    }

    /**
     * Sends a GET request to the given url and deserializes the response
     *
     * @param uri          where to send the GET request
     * @param responseType class to deserialize the response
     * @param <T>
     * @return
     */
    public <T> T get(String uri, Class<T> responseType) {
        return request(uri).request().get(responseType);
    }

    /**
     * Sends a POST request to the given url and deserializes the response.
     *
     * @param uri          where to send the POST request
     * @param entity       object to send to url. Is serialized as JSON
     * @param responseType class to deserialize the response
     * @param <T>
     * @return
     */
    public <T> T post(String uri, Object entity, Class<T> responseType) {
        Entity<Object> entityObject = Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE);
        return request(uri).request().post(entityObject, responseType);
    }

    /**
     * Build a new web resource target.
     *
     * @param uri
     * @return
     */
    public WebTarget request(String uri) {
        return testClient.target(getBaseUri() + uri);
    }

    /**
     * Get configured client
     *
     * @return
     */
    public Client getTestClient() {
        return testClient;
    }

    private UndertowJaxrsServer createTestContainer(int bindPort, String bindUrl) {
        long start = System.currentTimeMillis();
        UndertowJaxrsServer server = createServer(bindPort, bindUrl);
        ResteasyDeployment resteasyDeployment = createDeployment();
        DeploymentInfo deploymentInfo = createDeploymentInfo(server, resteasyDeployment);
        server.deploy(deploymentInfo);
        long duration = System.currentTimeMillis() - start;
        logger.info("Undertow with resteasy and jackson is running. " + duration + "ms needed.");
        return server;
    }

    private DeploymentInfo createDeploymentInfo(UndertowJaxrsServer server, ResteasyDeployment resteasyDeployment) {
        DeploymentInfo deploymentInfo = server.undertowDeployment(resteasyDeployment);
        deploymentInfo.setClassLoader(ResteasyTest.class.getClassLoader());
        deploymentInfo.setDeploymentName(this.getClass().getName());
        deploymentInfo.setContextPath("/");
        return deploymentInfo;
    }

    private ResteasyDeployment createDeployment() {
        ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
        resteasyDeployment.setApplication(configureApplication());
        resteasyDeployment.setResources(configureResources());
        resteasyDeployment.setProviders(configureProvider());
        return resteasyDeployment;
    }

    private UndertowJaxrsServer createServer(int bindPort, String bindUrl) {
        UndertowJaxrsServer server = new UndertowJaxrsServer();
        Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(bindPort, bindUrl);
        server.start(serverBuilder);
        return server;
    }
}
