package com.kdao.cmpe235_project.data;

public class Comment {
    private int id;
    private String username;
    private String comment;
    private int rating;
    private String treeId;
    private boolean islike;
    private String time;

    public Comment() {}

    public Comment(String username, int rating, String comment, String time, boolean islike) {
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.time = time;
        this.islike = islike;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername(){ return username;}

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public boolean getLike() {
        return islike;
    }

    public void setLike(boolean like) {
        this.islike = like;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
