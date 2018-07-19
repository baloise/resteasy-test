# ResteasyTest
ResteasyTest provides a convenient way to test JAX-RS resource in your JUnit test. It allows you to define you're REST endpoints in your tests classes and verify that the exposed resources work properly. ResteasyTest is similar to [JerseyTest](https://github.com/jersey/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTest.java) but uses [Resteasy](https://resteasy.github.io/) as provider and is not as feature rich. The dependencies used are the same as in JBoss EAP / WildFly. These are [undertow](http://undertow.io/) as ServletContainer, [Resteasy](https://resteasy.github.io/) as JAX-RS implementation and [jackson 2](https://github.com/FasterXML/jackson) for JSON support. 

# Setup dependencies
Because ResteasyTest isn't available as Maven dependency in the Maven Central Repository yet, you need to include it in your project yourself. If demanded, I will make the effort to include it. 

To get ResteasyTest running, you need to include the following Maven dependencies in your pom.xml in your `<dependencies></dependencies>` section:
```
<dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-servlet</artifactId>
    <version>${undertow.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-core</artifactId>
    <version>${undertow.version}</version>
    <scope>test</scope>    
</dependency>
<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-undertow</artifactId>
    <version>${resteasy.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-jaxrs</artifactId>
    <version>${resteasy.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-jackson2-provider</artifactId>
    <version>${resteasy.version}</version>
    <scope>test</scope>
</dependency>
```
To add JAXB annotation support, you might want to add these dependency too:
```
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-jaxb-annotations</artifactId>
    <version>${jackson.version}</version>
    <scope>test</scope>
</dependency>
```

For `java.time` support, you need to these dependency, if you're Jackson version is below 2.8.5, which is the case for JBoss EAP 7.0 or WildFly 10. 
```
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>${jackson.version}</version>
    <scope>test</scope>
</dependency>
```
If your Jackson version is above 2.8.5 (like in JBoss EAP 7.1+), you need to use the [jackson-modules-java8](https://github.com/FasterXML/jackson-modules-java8), but these aren't tested yet in this setup. 

### JBossEAP 7.0 / WildFly 10 dependency versions
For JBoss EAP 7.0 and WildFly 10 (upstream project), you need the following dependency versions:
```
<properties>
    <resteasy.version>3.0.16.Final</resteasy.version>
    <undertow.version>1.3.21.Final</undertow.version>
    <jackson.version>2.6.3</jackson.version>
</properties>
```
So you use the same artifacts as in your application server. 

### JBoss EAP 7.1 / WildFly 11 dependency versions
For JBoss EAP 7.1 and WildFly 11 (upstream project), you need the following dependency versions. This setup isn't tested from me yet but should work. If you use additional Jackson modules, make sure these are compatible with these versions.
```
<properties>
    <resteasy.version>3.0.24.Final</resteasy.version>
    <undertow.version>1.3.25.Final</undertow.version>
    <jackson.version>2.8.9</jackson.version>
</properties>
```

# Usage
To use ResteasyTest, you need to include [ResteasyTest.java](https://raw.githubusercontent.com/niiku/resteasy-test/master/src/main/java/io/nikio/jaxrs/ResteasyTest.java) in your maven module where you want to test a JAX-RS resource. 

To test a JAX-RS endpoint, create a test class and inherit the now included `ResteasyTest.java` class and override the method `configureResource()` to add an instance of your REST endpoint to ResteasyTest. 
```
public class RestEndpointTest extends ResteasyTest {
    @Override
    public List<Object> configureResources() {
        return Stream.of(new RestEndpoint()).collect(Collectors.toList());
    }
}
```
To declare a provider (eg. for custom field mappings) override `configureProvider()`.
```
@Override
public List<Object> configureProvider() {
    return Stream.of(new ObjectMapperContextResolver()).collect(Collectors.toList());
}
```

Now you can simply use `get()` and `post()` to test your endpoint:
```
@Test
public void testGetPojo() {
    Pojo expectedPojo = new Pojo("Hello");
    Pojo actualPojo = get("/resource/pojo", Pojo.class);
    Assert.assertEquals(expectedPojo, actualPojo);
}
@Test
public void testPostPojo() {
    Pojo world = new Pojo("World");
    Pojo response = post("/resource/post", world, Pojo.class);
    Assert.assertEquals("Hello World", response.getName());
}
```
To get the result without deserialization, simply put `String.class` as last argument to `post()` or `get()`. You find complete examples in [ResteasyTestTest.java](https://github.com/niiku/resteasy-test/blob/master/src/test/java/io/nikio/jaxrs/ResteasyTestTest.java). 


# CDI Support
ResteasyTest doesn't support CDI directly. But you can use [cdi-unit](http://bryncooke.github.io/cdi-unit/) to inject a REST endpoint in your JUnit test and return it in the overriden `configureResources()` method. But for the most cases I would recommend using [mockito](http://site.mockito.org/) with its `MockitoJUnitRunner.class` to satisfy `@Inject` points. `cdi-unit` is slow because it's using [Weld](http://weld.cdi-spec.org/). I think the purpose should be to test your REST endpoints serialization/deserialization and URL only. 