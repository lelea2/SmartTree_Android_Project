package com.kdao.cmpe235_project.data;

public class Comment {
    private int id;
    private User user;
    private String comment;
    private int rating;
    private String treeId;
    private boolean like;

    public Comment() {}

    public Comment(String comment, int rating, boolean like, User user) {
        this.comment = comment;
        this.rating = rating;
        this.like = like;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
