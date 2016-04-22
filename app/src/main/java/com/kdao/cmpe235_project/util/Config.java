package com.kdao.cmpe235_project.util;

//Maintain constant in Java
public class Config {
    public static final String GOOGLE_API = "AIzaSyBOV7pJDxO_vifta4XzE4b_RnDTs0NeV0o";
    public static final String YOUTUBE_API_KEY = "AIzaSyCplV4mTS_Pu-I7ccgGI3RAgmjNa7HDkAI";
    public static final int RADIUS = 20000;

    public static final String BASE_URL = "https://secure-dusk-26659.herokuapp.com";

    //Configuration for error message
    public static final String VALID_FORM = "Please fill out the form";
    public static final String VALID_EMAIL = "Please enter valid email";
    public static final String VALID_PWD = "Please enter matching confirmed password";
    public static final String REGISTER_ERR = "Error when register user. Please try again!";
    public static final String LOGIN_ERR = "Invalid email or password. Please try again!";
    public static final String LOGIN_BARCODE_ERR = "Invalid barcode for login. Please try a " +
            "different one";
    public static final String SCAN_ERR = "No scan data received!";
    public static final String URL_ENCODE_ERR = "An Exception given because of " +
            "UrlEncodedFormEntity argument :";
    public static final String SERVER_ERR = "Technical difficulty. Please try again!";
    public static final String REQUIRE_SIGNIN = "You need to log in to execute these action";
    public static final String CREATE_TREE_ERR = "Error creating new tree. Please try again!";

    //Configuration for informative msg
    public static final String NEW_TREE_CREATED = "New tree created";
    public static final String NEW_SENSOR_CREATED = "New sensor created";

    //Configuration for loading msg
    public static final String AUTHENTICATE = "Authenticating...";
    public static final String GET_TREES = "Getting All Trees...";
    public static final String GET_TREE_INFO = "Get tree info...";
    public static final String CREATE_TREE = "Generate New Tree...";
    public static final String GET_SENSORS = "Getting All Sensors...";

    //FLAG key configuration
    public static final String SIGN_IN_WITH_BARCODE = "SIGN_IN_WITH_BARCODE";
    public static final String VIEW_TREE_WITH_BARCODE = "VIEW_TREE_WITH_BARCODE";
    public static final String SIGN_IN_WITH_BARCODE_ERR = "SIGN_IN_WITH_BARCODE_ERR";
    public static final String TREE_SESSION_ID = "TREE_SESSION_ID";
    public static final String SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED";
    public static final String TREE_ACTIVITY = "TREE_ACTIVITY";
    public static final String SENSOR_ACTIVITY = "SENSOR_ACTIVITY";

    //Config for ROLE
    public static final int ADMIN_ROLE = 1;
    public static final int USER_ROLE = 2;
}
