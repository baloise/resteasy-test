package com.baloise.jaxrs;

import com.baloise.jaxrs.beans.Pojo;
import com.baloise.jaxrs.resource.RestEndpoint;
import com.baloise.jaxrs.resource.TestApplication;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationTest extends ResteasyTest {

    @Override
    public Application configureApplication() {
        return new TestApplication();
    }

    @Override
    public List<Object> configureResources() {
        return Stream.of(new RestEndpoint()).collect(Collectors.toList());
    }

    @Test
    public void testGetPojo() {
        Pojo expectedPojo = new Pojo("Hello");

        Pojo actualPojo = get("/api/resource/pojo", Pojo.class);

        Assert.assertEquals(expectedPojo, actualPojo);
    }
}
