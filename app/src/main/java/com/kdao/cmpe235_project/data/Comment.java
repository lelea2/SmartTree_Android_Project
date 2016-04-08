package com.kdao.cmpe235_project.data;

/**
 * Created by kdao on 4/2/16.
 */
public class Comment {
    private int id;
    private User user;
    private String comment;
    private int rating;
    private String treeId;
    private boolean like;

    public Comment(String comment, int rating, boolean like, User user) {
        this.comment = comment;
        this.rating = rating;
        this.like = like;
        this.user = user;
    }
}
