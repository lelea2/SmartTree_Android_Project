package com.kdao.cmpe235_project.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which has Utility methods
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Function checking for valid email
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Checking for is empty string function
     * @param txt
     * @return
     */
    public static boolean isEmptyString(String txt){
        return (txt != null && txt.trim().length() > 0) ? false : true;
    }
}