package com.kdao.cmpe235_project.util;

//Maintain constant in Java
public class Config {
    public static final String GOOGLE_API = "AIzaSyBhy-ej1Fo7fZ85_LWoJ1APY8OJ6yzNhzc";
    public static final String YOUTUBE_API_KEY = "AIzaSyBhy-ej1Fo7fZ85_LWoJ1APY8OJ6yzNhzc";
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
    public static final String CREATE_SENSOR_ERR = "Error creating new sensor. Please try again!";
    public static final String COMMENT_PER_TREE = "Please select a specific tree to view comment";
    public static final String NO_COMMENTS = "No comments available for the current tree";
    public static final String NO_SENSORS = "No sensors available for the current tree";
    public static final String SENSOR_NO_DEPLOY = "Please deploy sensor before you could interact" +
            " with it";
    public static final String SENSOR_NO_AVAIL = "Sensor you choose is not available. Please try " +
            "another one";
    public static final String SENSOR_DEPLOYED = "Sensor already deployed to a tree. Please " +
            "choose another one";
    public static final String DEPLOY_ERR = "Please choose a tree and sensor before you could " +
            "execute deployment";
    public static final String COMMENT_NO_SIGNIN = "Please sign in to your account to be able to " +
            "add comemnt";

    //Configuration for informative msg
    public static final String NEW_TREE_CREATED = "New tree created";
    public static final String NEW_SENSOR_CREATED = "New sensor created";
    public static final String NEW_COMMENT_CREATED = "Comment added";
    public static final String TREE_DEPLOYED = "Sensor deployed to tree";
    public static final String SENSOR_UPDATED = "Sensor is updated successfully";
    public static final String SENSOR_DELETED = "Sensor is undeployed from tree";


    //Configuration for loading msg
    public static final String AUTHENTICATE = "Authenticating...";
    public static final String GET_TREES = "Getting All Trees...";
    public static final String GET_TREE_INFO = "Get tree info...";
    public static final String CREATE_TREE = "Generate New Tree...";
    public static final String CREATE_SENSOR = "Generate New Sensor...";
    public static final String CREATE_COMMENT = "Create comment...";
    public static final String GET_SENSORS = "Getting All Sensors...";
    public static final String GET_SENSOR = "Getting Sensor detail...";
    public static final String GET_COMMENTS = "Getting Comments...";
    public static final String DEPLOY_TREE = "Deploy sensor to tree...";
    public static final String UPDATE_SENSOR = "Update sensor...";
    public static final String UNDEPLOY_SENSOR = "Undeploying sensor...";

    //FLAG key configuration
    public static final String SIGN_IN_WITH_BARCODE = "SIGN_IN_WITH_BARCODE";
    public static final String VIEW_TREE_WITH_BARCODE = "VIEW_TREE_WITH_BARCODE";
    public static final String SIGN_IN_WITH_BARCODE_ERR = "SIGN_IN_WITH_BARCODE_ERR";
    public static final String TREE_SESSION_ID = "TREE_SESSION_ID";
    public static final String TREE_SESSION_NAME = "TREE_SESSION_NAME";
    public static final String SENSOR_SESSION_ID = "SENSOR_SESSION_ID";
    public static final String SENSOR_SESSION_TYPE = "SENSOR_SESSION_TYPE";
    public static final String SENSOR_SESSION_NAME = "SENSOR_SESSION_NAME";
    public static final String COMMENT_SESSION_ID = "COMMENT_SESSION_ID";
    public static final String SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED";
    public static final String TREE_ACTIVITY = "TREE_ACTIVITY";
    public static final String SENSOR_ACTIVITY = "SENSOR_ACTIVITY";
    public static final String TREE_DEPLOY_ACTIVITY = "TREE_DEPLOY_ACTIVITY";

    //Title for sensor dialog box
    public static final String SENSOR_TITLE = "Sensor update";
    public static final String SENSOR_UPDATE_REQUIRED = "Please log in to be able to update sensor";
    public static final String DIALOG_TRY_AGAIN = "Try again";

    public static final String TREE_TITLE = "Tree Information View";
    public static final String TREE_NO_EXIST = "No information for tree exist. Please try again";

    public static final String SENSOR_UNDEPLOY_TITLE = "Undeploy Sensor";
    public static final String SENSOR_UNDEPLOY_CHECK = "Are you sure to undeploy your tree?";
    public static final String ROBOT_NOT_ONLINE = "Robot is not online";
    public static final String ROBOT_DISCONNECTED = "Robot is disconnected";
    public static final String ROBOT_NO_INTERACT = "No interaction corresponding to this sensor";
    public static final String SET_LED = "Light up sphero";

    //Config for ROLE
    public static final int ADMIN_ROLE = 1;
    public static final int USER_ROLE = 2;

    //Sphero configuration
    public static final String SPHERO_NOT_GRANTED = "Location permission has not already been granted";
    public static final String SPHERO_GRANTED = "Location permission already granted";

    //1. water
    //2. light
    //3. speed
    //4. voice
    public static final int WATER_SENSOR = 1;
    public static final int LIGHT_SENSOR = 2;
    public static final int SPEED_SENSOR = 3;
    public static final int VOICE_SENSOR = 4;
}
