package com.baloise.jaxrs;

import com.baloise.jaxrs.beans.Pojo;
import com.baloise.jaxrs.provider.ObjectMapperContextResolver;
import com.baloise.jaxrs.resource.RestEndpoint;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResteasyTestTest extends ResteasyTest {
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
        Pojo expectedPojo = new Pojo("Hello");

        Pojo actualPojo = get("/resource/pojo", Pojo.class);

        Assert.assertEquals(expectedPojo, actualPojo);
    }

    @Test
    public void testGetLocalDate() {
        LocalDate expectedDate = LocalDate.now();

        LocalDate localDate = get("/resource/date", LocalDate.class);
        String localDateString = localDate.toString();

        Assert.assertEquals("0000-00-00".length(), localDateString.length());
        Assert.assertEquals(expectedDate, localDate);
    }

    @Test
    public void testPostPojo() {
        Pojo world = new Pojo("World");

        Pojo response = post("/resource/post", world, Pojo.class);

        Assert.assertEquals("Hello World", response.getName());
    }
}
