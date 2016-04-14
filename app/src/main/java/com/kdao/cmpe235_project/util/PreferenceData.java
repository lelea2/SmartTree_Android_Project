package com.kdao.cmpe235_project.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Set preference data for user
 */
public class PreferenceData {
    static final String PREF_LOGGEDIN_USER_EMAIL = "logged_in_email";
    static final String PREF_USER_LOGGEDIN_STATUS = "logged_in_status";
    static final String PREF_LOGGEDIN_USER_ID = "logged_in_id";

    /**
     * Get SharePreferences object
     * @param ctx
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Set user email in preference data
     * @param ctx
     * @param email
     */
    public static void setLoggedInUserEmail(Context ctx, String email) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGGEDIN_USER_EMAIL, email);
        editor.commit();
    }

    /**
     * Get user email in preference data
     * @param ctx
     * @return
     */
    public static String getLoggedInEmailUser(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LOGGEDIN_USER_EMAIL, "");
    }

    /**
     * Set user id in preference data
     * @param ctx
     * @param uid
     */
    public static void setLoggedInUserId(Context ctx, String uid) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGGEDIN_USER_ID, uid);
        editor.commit();
    }

    /**
     * Get userId in preference data
     * @param ctx
     * @return
     */
    public static String getLoggedInUserId(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LOGGEDIN_USER_ID, "");
    }

    /**
     * Set user logged in status in preference data
     * @param ctx
     * @param status
     */
    public static void setUserLoggedInStatus(Context ctx, boolean status) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_USER_LOGGEDIN_STATUS, status);
        editor.commit();
    }

    /**
     * Get user loggedin status in preference data
     * @param ctx
     * @return
     */
    public static boolean getUserLoggedInStatus(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_USER_LOGGEDIN_STATUS, false);
    }

    /**
     * Clear all user information in preference data
     * @param ctx
     */
    public static void clearLoggedInEmailAddress(Context ctx) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_LOGGEDIN_USER_EMAIL);
        editor.remove(PREF_USER_LOGGEDIN_STATUS);
        editor.commit();
    }
}