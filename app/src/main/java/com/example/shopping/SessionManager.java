package com.example.shopping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences preference;

    // Editor for Shared preferenceerences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared preference mode
    int PRIVATE_MODE = 0;

    // Sharedpreference file name
    static final String PREF_NAME = "Reg";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_ID = "id";
    public static final String KEY_MONEY = "money";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_UID = "UID";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.context = context;
        preference = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preference.edit();

    }
    // login id passing
    public void CreateWalletBalanceSession(String id) {
        editor.putString(KEY_MONEY, id);
        editor.commit();
    }

    public void createDetailsSession(String name, String email, String phone, String UID) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_UID, UID);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> data = new HashMap<>();
        // user name
        data.put(KEY_NAME, preference.getString(KEY_NAME, null));
        // user email id
        data.put(KEY_EMAIL, preference.getString(KEY_EMAIL, null));

        data.put(KEY_PHONE, preference.getString(KEY_PHONE, null));
        //
        data.put(KEY_UID, preference.getString(KEY_UID, null));
        // return user
        return data;
    }

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            context.startActivity(i);
        }
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Add new Flag to start new Activity
        //   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(i);

    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return preference.getBoolean(IS_LOGIN, false);

    }

}
