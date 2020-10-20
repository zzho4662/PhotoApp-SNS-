package com.jhjang.photoapp.model;

public class Post {
    private int id;
    private int friendUserId;
    private String photoUrl;
    private String content;
    private String createdAt;

    public Post() {
    }

    public Post(int id, int friendUserId, String photoUrl, String content, String createdAt) {
        this.id = id;
        this.friendUserId = friendUserId;
        this.photoUrl = photoUrl;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(int friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
