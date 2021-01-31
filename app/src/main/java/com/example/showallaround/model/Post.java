package com.example.showallaround.model;

public class Post {

    private String text;
    private String media_url;
    private String id;
    private int comments_count;
    private int likes_count;
    private boolean origin;

    public boolean isOrigin() {
        return origin;
    }

    public void setOrigin(boolean origin) {
        this.origin = origin;
    }

    public Post(String id, String text, String media_url, int comments_count, int likes_count, boolean origin) {
        this.text = text;
        this.media_url = media_url;
        this.id = id;
        this.comments_count = comments_count;
        this.likes_count = likes_count;
        this.origin = origin;
    }

    public Post(String text, String media_url) {
        this.text = text;
        this.id = "";
        this.media_url = media_url;
        this.comments_count = 0;
        this.likes_count = 0;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public String getText() {
        return text;
    }

    public String getMedia_url() {
        return media_url;
    }

    public String getId() {
        return id;
    }

    public String getComments_count() {
        return String.valueOf(comments_count);
    }

    public String getLikes_count() {
        return String.valueOf(likes_count);
    }
}
