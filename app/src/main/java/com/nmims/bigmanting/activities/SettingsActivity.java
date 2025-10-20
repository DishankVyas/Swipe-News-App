package com.nmims.bigmanting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.nmims.bigmanting.R;
import com.nmims.bigmanting.utils.SharedPrefsHelper;

/**
 * SettingsActivity - App settings and preferences
 * Includes theme toggle, API key configuration, and logout
 */
public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat darkModeSwitch;
    private EditText apiKeyInput;
    private Button btnSaveApiKey;
    private Button btnLogout;
    private Toolbar toolbar;

    private SharedPrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize preferences helper
        prefsHelper = new SharedPrefsHelper(this);

        // Initialize views
        initViews();

        // Set up toolbar with back button
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        // Load current settings
        loadSettings();

        // Set up listeners
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        apiKeyInput = findViewById(R.id.et_api_key);
        btnSaveApiKey = findViewById(R.id.btn_save_api_key);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadSettings() {
        // Load dark mode preference
        boolean isDarkMode = prefsHelper.isDarkMode();
        darkModeSwitch.setChecked(isDarkMode);

        // Load API key if exists
        String savedApiKey = prefsHelper.getNewsApiKey();
        if (!savedApiKey.isEmpty()) {
            // Mask API key for security
            apiKeyInput.setText(maskApiKey(savedApiKey));
        }
    }

    private void setupListeners() {
        // Dark mode toggle
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsHelper.setDarkMode(isChecked);
            applyTheme(isChecked);
        });

        // Save API key
        btnSaveApiKey.setOnClickListener(v -> saveApiKey());

        // Logout button
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    /**
     * Apply theme based on preference
     */
    private void applyTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Recreate activity to apply theme
        recreate();
    }

    /**
     * Save NewsAPI key to preferences
     */
    private void saveApiKey() {
        String apiKey = apiKeyInput.getText().toString().trim();

        if (apiKey.isEmpty()) {
            Toast.makeText(this, "Please enter an API key", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate API key format (basic check)
        if (apiKey.length() < 20) {
            Toast.makeText(this, "Invalid API key format", Toast.LENGTH_SHORT).show();
            return;
        }

        prefsHelper.setNewsApiKey(apiKey);
        Toast.makeText(this, "API key saved successfully!", Toast.LENGTH_SHORT).show();

        // Mask the saved key
        apiKeyInput.setText(maskApiKey(apiKey));
    }

    /**
     * Mask API key for display (show only first and last 4 characters)
     */
    private String maskApiKey(String apiKey) {
        if (apiKey.length() <= 8) {
            return apiKey;
        }
        String start = apiKey.substring(0, 4);
        String end = apiKey.substring(apiKey.length() - 4);
        return start + "..." + end;
    }

    /**
     * Show logout confirmation dialog
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Logout user and return to login screen
     */
    private void logout() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Clear preferences
        prefsHelper.clearAll();

        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
