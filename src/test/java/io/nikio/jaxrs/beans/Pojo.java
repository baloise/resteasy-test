package io.nikio.jaxrs.beans;

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
}
