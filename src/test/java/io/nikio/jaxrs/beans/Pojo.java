package com.baloise.jaxrs.beans;

import java.util.Objects;

public class Pojo {
    private String name;

    public Pojo() {
    }

    public Pojo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pojo pojo = (Pojo) o;
        return Objects.equals(name, pojo.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
