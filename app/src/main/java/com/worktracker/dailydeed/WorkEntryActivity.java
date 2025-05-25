package com.worktracker.dailydeed;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.worktracker.dailydeed.database.DatabaseHelper;
import com.worktracker.dailydeed.models.WorkEntry;
import com.worktracker.dailydeed.utils.PreferenceManager;
import com.worktracker.dailydeed.utils.TimeUtils;

import java.util.Calendar;

public class WorkEntryActivity extends AppCompatActivity {
    
    private EditText etTime, etLocation, etTaskDescription, etDuration, etNotes;
    private Button btnCurrentTime, btnCurrentLocation, btnCancel, btnSave;
    
    private DatabaseHelper databaseHelper;
    private PreferenceManager preferenceManager;
    private WorkEntry editingEntry;
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_entry);
        
        initializeViews();
        initializeDatabase();
        setupClickListeners();
        checkForEditMode();
        setDefaultValues();
    }
    
    private void initializeViews() {
        etTime = findViewById(R.id.etTime);
        etLocation = findViewById(R.id.etLocation);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etDuration = findViewById(R.id.etDuration);
        etNotes = findViewById(R.id.etNotes);
        btnCurrentTime = findViewById(R.id.btnCurrentTime);
        btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
    }
    
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        preferenceManager = new PreferenceManager(this);
    }
    
    private void setupClickListeners() {
        etTime.setOnClickListener(v -> showTimePicker());
        
        btnCurrentTime.setOnClickListener(v -> {
            String currentTime = preferenceManager.is24HourFormat() ? 
                    TimeUtils.getCurrentTime() : TimeUtils.getCurrentTime12Hour();
            etTime.setText(currentTime);
        });
        
        btnCurrentLocation.setOnClickListener(v -> {
            // For now, set a default location. In a real app, you'd use GPS
            etLocation.setText("Current Location");
            Toast.makeText(this, "Location feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        btnCancel.setOnClickListener(v -> finish());
        
        btnSave.setOnClickListener(v -> saveWorkEntry());
    }
    
    private void checkForEditMode() {
        long entryId = getIntent().getLongExtra("entry_id", -1);
        if (entryId != -1) {
            isEditMode = true;
            editingEntry = databaseHelper.getWorkEntry(entryId);
            if (editingEntry != null) {
                populateFields();
                btnSave.setText(getString(R.string.update_entry));
            }
        }
    }
    
    private void setDefaultValues() {
        if (!isEditMode) {
            // Set current time as default
            String currentTime = preferenceManager.is24HourFormat() ? 
                    TimeUtils.getCurrentTime() : TimeUtils.getCurrentTime12Hour();
            etTime.setText(currentTime);
        }
    }
    
    private void populateFields() {
        if (editingEntry != null) {
            etTime.setText(editingEntry.getTime());
            etLocation.setText(editingEntry.getLocation());
            etTaskDescription.setText(editingEntry.getTaskDescription());
            etDuration.setText(String.valueOf(editingEntry.getDuration()));
            etNotes.setText(editingEntry.getNotes());
        }
    }
    
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String time;
                    if (preferenceManager.is24HourFormat()) {
                        time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    } else {
                        String amPm = selectedHour >= 12 ? "PM" : "AM";
                        int displayHour = selectedHour == 0 ? 12 : (selectedHour > 12 ? selectedHour - 12 : selectedHour);
                        time = String.format("%02d:%02d %s", displayHour, selectedMinute, amPm);
                    }
                    etTime.setText(time);
                }, hour, minute, preferenceManager.is24HourFormat());
        
        timePickerDialog.show();
    }
    
    private void saveWorkEntry() {
        String time = etTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        
        // Validation
        if (TextUtils.isEmpty(time)) {
            etTime.setError("Time is required");
            etTime.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(taskDescription)) {
            etTaskDescription.setError("Task description is required");
            etTaskDescription.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(durationStr)) {
            etDuration.setError("Duration is required");
            etDuration.requestFocus();
            return;
        }
        
        double duration;
        try {
            duration = Double.parseDouble(durationStr);
            if (duration < 0) {
                etDuration.setError("Duration must be positive");
                etDuration.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etDuration.setError("Invalid duration format");
            etDuration.requestFocus();
            return;
        }
        
        String currentProfile = preferenceManager.getCurrentProfile();
        
        if (isEditMode && editingEntry != null) {
            // Update existing entry
            editingEntry.setTime(time);
            editingEntry.setLocation(location);
            editingEntry.setTaskDescription(taskDescription);
            editingEntry.setDuration(duration);
            editingEntry.setNotes(notes);
            editingEntry.setProfileName(currentProfile);
            
            int rowsAffected = databaseHelper.updateWorkEntry(editingEntry);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Work entry updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update work entry", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create new entry
            WorkEntry entry = new WorkEntry(time, location, taskDescription, duration, notes, currentProfile);
            long id = databaseHelper.addWorkEntry(entry);
            
            if (id != -1) {
                Toast.makeText(this, "Work entry saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save work entry", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}