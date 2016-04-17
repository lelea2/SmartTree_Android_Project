package com.kdao.cmpe235_project.util;

//Maintain constant in Java
public class Config {
    public static final String GOOGLE_API = "AIzaSyBOV7pJDxO_vifta4XzE4b_RnDTs0NeV0o";
    public static final int RADIUS = 20000;

    public static final String BASE_URL = "https://secure-dusk-26659.herokuapp.com";

    //Configuration for error message
    public static final String VALID_FORM = "Please fill out the form";
    public static final String VALID_EMAIL = "Please enter valid email";
    public static final String VALID_PWD = "Please enter matching confirmed password";
    public static final String REGISTER_ERR = "Error when register user. Please try again!";
    public static final String LOGIN_ERR = "Invalid email or password. Please try again!";
    public static final String SCAN_ERR = "No scan data received!";
    public static final String URL_ENCODE_ERR = "An Exception given because of " +
            "UrlEncodedFormEntity argument :";

    //FLAG key configuration
    public static final String SIGN_IN_WITH_BARCODE = "SIGN_IN_WITH_BARCODE";
}
