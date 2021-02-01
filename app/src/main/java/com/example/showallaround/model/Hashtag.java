package com.example.showallaround.model;

public class Hashtag {
    private String name;
    private String query;

    public Hashtag(String name, String query) {
        this.name = name;
        this.query = query;
    }
    public Hashtag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
