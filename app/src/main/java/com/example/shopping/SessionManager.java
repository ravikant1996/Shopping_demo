package com.example.shopping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    public static final String KEY_AID = "aid";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_UID = "UID";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String PROPERTY_KEY = "propertyId";
    public static final String PROPERTYTYPE_KEY = "propertytype";
    public static final String PROPERTYFOR_KEY = "propertyfor";
    public static final String PROPERTYSUBTYPE_KEY = "propertysubtype";

    public static final String FAVORITES = "favorite";

    public static final String BEDROOM_KEY = "bedroom";
    public static final String S_PROPERTYKEY = "s_keyId";
    public static final String S_PROPERTYTYPE = "s_type";
    public static final String S_ADDRESS = "s_address";
    public static final String S_BEDROOM = "s_bedroom";
    public static final String S_PRICE = "s_price";
    public static final String S_TIME = "s_time";


    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.context = context;
        preference = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preference.edit();

    }

    //Search session
    public void createSearchSession(String propertyType, String propertyfor) {
        editor.putString(PROPERTYTYPE_KEY, propertyType);
        editor.putString(PROPERTYFOR_KEY, propertyfor);
        editor.commit();
    }

    public HashMap<String, String> getPropertySearchSession() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PROPERTYTYPE_KEY, preference.getString(PROPERTYTYPE_KEY, null));
        data.put(PROPERTYFOR_KEY, preference.getString(PROPERTYFOR_KEY, null));
        return data;
    }

    // login id passing
    public void CreateWalletBalanceSession(String id) {
        editor.putString(KEY_MONEY, id);
        editor.commit();
    }

    public HashMap<String, String> getWalletBalance() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(KEY_MONEY, preference.getString(KEY_MONEY, null));
        return data;
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


    public void saveFavorites(List<transaction> history) {
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(history);
        editor.clear();
        editor.putString(FAVORITES, jsonFavorites);
        editor.commit();
    }

    public void addFavorite(transaction product) {
        List<transaction> history = getFavorites();
        if (history == null)
            history = new ArrayList<transaction>();
        history.add(product);
        saveFavorites(history);
    }

    public ArrayList<transaction> getFavorites() {
        List<transaction> history;

        if (preference.contains(FAVORITES)) {
            String jsonFavorites = preference.getString(FAVORITES, null);
            Gson gson = new Gson();
            transaction[] favoriteItems = gson.fromJson(jsonFavorites,
                    transaction[].class);

            history = Arrays.asList(favoriteItems);
            history = new ArrayList<transaction>(history);
        } else
            return null;

        return (ArrayList<transaction>) history;
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
