package com.worktracker.dailydeed;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worktracker.dailydeed.adapters.ProfileAdapter;
import com.worktracker.dailydeed.database.DatabaseHelper;
import com.worktracker.dailydeed.models.Profile;
import com.worktracker.dailydeed.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileManagerActivity extends AppCompatActivity implements ProfileAdapter.OnProfileClickListener {
    
    private EditText etNewProfileName;
    private Button btnCreateProfile;
    private TextView tvCurrentProfile;
    private RecyclerView rvProfiles;
    private LinearLayout layoutEmptyState;
    
    private ProfileAdapter adapter;
    private DatabaseHelper databaseHelper;
    private PreferenceManager preferenceManager;
    
    private List<Profile> profiles;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manager);
        
        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadProfiles();
        updateCurrentProfile();
    }
    
    private void initializeViews() {
        etNewProfileName = findViewById(R.id.etNewProfileName);
        btnCreateProfile = findViewById(R.id.btnCreateProfile);
        tvCurrentProfile = findViewById(R.id.tvCurrentProfile);
        rvProfiles = findViewById(R.id.rvProfiles);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
    }
    
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        preferenceManager = new PreferenceManager(this);
    }
    
    private void setupRecyclerView() {
        profiles = new ArrayList<>();
        adapter = new ProfileAdapter(this, profiles);
        adapter.setOnProfileClickListener(this);
        
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        rvProfiles.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        btnCreateProfile.setOnClickListener(v -> createNewProfile());
    }
    
    private void loadProfiles() {
        profiles.clear();
        profiles.addAll(databaseHelper.getAllProfiles());
        adapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (profiles.isEmpty()) {
            rvProfiles.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvProfiles.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
    
    private void updateCurrentProfile() {
        String currentProfile = preferenceManager.getCurrentProfile();
        tvCurrentProfile.setText(currentProfile);
    }
    
    private void createNewProfile() {
        String profileName = etNewProfileName.getText().toString().trim();
        
        if (TextUtils.isEmpty(profileName)) {
            etNewProfileName.setError("Profile name is required");
            etNewProfileName.requestFocus();
            return;
        }
        
        // Check if profile already exists
        for (Profile profile : profiles) {
            if (profile.getName().equalsIgnoreCase(profileName)) {
                etNewProfileName.setError("Profile already exists");
                etNewProfileName.requestFocus();
                return;
            }
        }
        
        // Create new profile
        Profile newProfile = new Profile(profileName);
        long id = databaseHelper.addProfile(newProfile);
        
        if (id != -1) {
            etNewProfileName.setText("");
            loadProfiles();
            Toast.makeText(this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to create profile", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onLoadClick(Profile profile) {
        new AlertDialog.Builder(this)
                .setTitle("Load Profile")
                .setMessage("Switch to profile '" + profile.getName() + "'?")
                .setPositiveButton("Load", (dialog, which) -> {
                    databaseHelper.setActiveProfile(profile.getName());
                    preferenceManager.setCurrentProfile(profile.getName());
                    updateCurrentProfile();
                    Toast.makeText(this, "Profile loaded: " + profile.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onDeleteClick(Profile profile) {
        if ("Default".equals(profile.getName())) {
            Toast.makeText(this, "Cannot delete the default profile", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete profile '" + profile.getName() + 
                           "'?\n\nThis will also delete all work entries for this profile.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // If deleting the current active profile, switch to Default
                    if (profile.getName().equals(preferenceManager.getCurrentProfile())) {
                        databaseHelper.setActiveProfile("Default");
                        preferenceManager.setCurrentProfile("Default");
                        updateCurrentProfile();
                    }
                    
                    databaseHelper.deleteProfile(profile.getName());
                    loadProfiles();
                    Toast.makeText(this, "Profile deleted: " + profile.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}