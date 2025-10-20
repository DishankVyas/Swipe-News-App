package com.nmims.bigmanting.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class for SharedPreferences
 * Stores user preferences like theme, category filters, etc.
 */
public class SharedPrefsHelper {

    private static final String PREF_NAME = "SwipeNewsPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_SELECTED_CATEGORY = "selected_category";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_NEWS_API_KEY = "news_api_key";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SharedPrefsHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Login status
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // User email
    public void setUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    // Selected category
    public void setSelectedCategory(String category) {
        editor.putString(KEY_SELECTED_CATEGORY, category).apply();
    }

    public String getSelectedCategory() {
        return prefs.getString(KEY_SELECTED_CATEGORY, "general");
    }

    // Dark mode
    public void setDarkMode(boolean isDarkMode) {
        editor.putBoolean(KEY_DARK_MODE, isDarkMode).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    // Country
    public void setCountry(String country) {
        editor.putString(KEY_COUNTRY, country).apply();
    }

    public String getCountry() {
        return prefs.getString(KEY_COUNTRY, "us");
    }

    // News API Key
    public void setNewsApiKey(String apiKey) {
        editor.putString(KEY_NEWS_API_KEY, apiKey).apply();
    }

    public String getNewsApiKey() {
        return prefs.getString(KEY_NEWS_API_KEY, "");
    }

    // Clear all preferences (logout)
    public void clearAll() {
        editor.clear().apply();
    }
}
