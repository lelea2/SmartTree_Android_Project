package com.kdao.cmpe235_project.data;

/**
 * Created by kdao on 4/2/16.
 */
public class User {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNum;
    private String password;
    private Role role;
    private String img;

    public User(String userId, String firstName, String lastName, String email, String phoneNum, String password, Role role, String img) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNum = phoneNum;
        this.password = password;
        this.role = role;
        this.img = img;
    }
}
