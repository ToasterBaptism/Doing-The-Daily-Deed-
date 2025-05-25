package com.worktracker.dailydeed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.worktracker.dailydeed.database.DatabaseHelper;
import com.worktracker.dailydeed.models.Profile;
import com.worktracker.dailydeed.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    
    private TextView tvCurrentProfile, tvTodayEntries, tvTodayHours;
    private Button btnNewEntry, btnViewEntries, btnProfileManager, btnSettings;
    
    private DatabaseHelper databaseHelper;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        initializeDatabase();
        setupClickListeners();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
    
    private void initializeViews() {
        tvCurrentProfile = findViewById(R.id.tvCurrentProfile);
        tvTodayEntries = findViewById(R.id.tvTodayEntries);
        tvTodayHours = findViewById(R.id.tvTodayHours);
        btnNewEntry = findViewById(R.id.btnNewEntry);
        btnViewEntries = findViewById(R.id.btnViewEntries);
        btnProfileManager = findViewById(R.id.btnProfileManager);
        btnSettings = findViewById(R.id.btnSettings);
    }
    
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        preferenceManager = new PreferenceManager(this);
        
        // Ensure we have an active profile
        Profile activeProfile = databaseHelper.getActiveProfile();
        if (activeProfile != null) {
            preferenceManager.setCurrentProfile(activeProfile.getName());
        }
    }
    
    private void setupClickListeners() {
        btnNewEntry.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WorkEntryActivity.class);
            startActivity(intent);
        });
        
        btnViewEntries.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewEntriesActivity.class);
            startActivity(intent);
        });
        
        btnProfileManager.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileManagerActivity.class);
            startActivity(intent);
        });
        
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
    
    private void updateUI() {
        String currentProfileName = preferenceManager.getCurrentProfile();
        tvCurrentProfile.setText(currentProfileName);
        
        // Update today's statistics
        int todayEntries = databaseHelper.getTodayWorkEntries(currentProfileName).size();
        double todayHours = databaseHelper.getTodayHoursForProfile(currentProfileName);
        
        tvTodayEntries.setText(String.valueOf(todayEntries));
        tvTodayHours.setText(String.format("%.1f", todayHours));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}