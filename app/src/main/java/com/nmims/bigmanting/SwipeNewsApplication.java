package com.nmims.bigmanting;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nmims.bigmanting.utils.SharedPrefsHelper;

/**
 * Custom Application class for SwipeNews
 * Enables Firestore offline persistence for cached reads/writes
 * This allows users to access saved articles even when offline
 */
public class SwipeNewsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Enable Firestore offline persistence
        // This allows data to be cached locally and synced when online
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // Enable offline persistence
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED) // Unlimited cache
                .build();

        db.setFirestoreSettings(settings);

        // Apply saved theme preference on app start
        SharedPrefsHelper prefsHelper = new SharedPrefsHelper(this);
        if (prefsHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
