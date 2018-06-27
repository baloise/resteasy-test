package io.nikio.jaxrs;

import io.nikio.jaxrs.beans.Pojo;
import io.nikio.jaxrs.provider.ObjectMapperContextResolver;
import io.nikio.jaxrs.resource.RestEndpoint;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResteasyTestTest extends RestEasyTest {
    @Override
    public List<Object> configureResources() {
        return Stream.of(new RestEndpoint()).collect(Collectors.toList());
    }

    @Override
    public List<Object> configureProvider() {
        return Stream.of(new ObjectMapperContextResolver()).collect(Collectors.toList());
    }

    @Test
    public void testGetPojo() {
        Pojo pojo = get("/resource/pojo", Pojo.class);

        Assert.assertEquals("Hello", pojo.getName());
    }

    @Test
    public void testGetLocalDate() {
        String localDate = get("/resource/date", String.class);

        Assert.assertEquals("\"0000-00-00\"".length(), localDate.length());
    }

    @Test
    public void testPostPojo() {
        Pojo world = new Pojo("World");

        Pojo response = post("/resource/post", world, Pojo.class);

        Assert.assertEquals("Hello World", response.getName());
    }
}
