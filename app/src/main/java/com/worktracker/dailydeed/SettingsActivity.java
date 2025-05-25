package com.worktracker.dailydeed;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.worktracker.dailydeed.database.DatabaseHelper;
import com.worktracker.dailydeed.utils.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    
    private Switch switchAutoLocation, switch24HourFormat;
    private Button btnBackupData, btnRestoreData;
    private TextView tvAppVersion, tvTotalEntries;
    
    private PreferenceManager preferenceManager;
    private DatabaseHelper databaseHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initializeViews();
        initializeDatabase();
        setupClickListeners();
        loadSettings();
        updateAppInfo();
    }
    
    private void initializeViews() {
        switchAutoLocation = findViewById(R.id.switchAutoLocation);
        switch24HourFormat = findViewById(R.id.switch24HourFormat);
        btnBackupData = findViewById(R.id.btnBackupData);
        btnRestoreData = findViewById(R.id.btnRestoreData);
        tvAppVersion = findViewById(R.id.tvAppVersion);
        tvTotalEntries = findViewById(R.id.tvTotalEntries);
    }
    
    private void initializeDatabase() {
        preferenceManager = new PreferenceManager(this);
        databaseHelper = new DatabaseHelper(this);
    }
    
    private void setupClickListeners() {
        switchAutoLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setAutoLocation(isChecked);
            if (isChecked) {
                Toast.makeText(this, "Auto location feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
        
        switch24HourFormat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.set24HourFormat(isChecked);
            Toast.makeText(this, "Time format updated", Toast.LENGTH_SHORT).show();
        });
        
        btnBackupData.setOnClickListener(v -> {
            // Backup functionality would be implemented here
            Toast.makeText(this, "Backup feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnRestoreData.setOnClickListener(v -> {
            // Restore functionality would be implemented here
            Toast.makeText(this, "Restore feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadSettings() {
        switchAutoLocation.setChecked(preferenceManager.isAutoLocationEnabled());
        switch24HourFormat.setChecked(preferenceManager.is24HourFormat());
    }
    
    private void updateAppInfo() {
        // Set app version
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvAppVersion.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tvAppVersion.setText("Unknown");
        }
        
        // Set total entries count
        String currentProfile = preferenceManager.getCurrentProfile();
        int totalEntries = databaseHelper.getEntryCountForProfile(currentProfile);
        tvTotalEntries.setText(String.valueOf(totalEntries));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}