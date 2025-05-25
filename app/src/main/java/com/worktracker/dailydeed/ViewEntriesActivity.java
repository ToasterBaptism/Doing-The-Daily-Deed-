package com.worktracker.dailydeed;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worktracker.dailydeed.adapters.WorkEntryAdapter;
import com.worktracker.dailydeed.database.DatabaseHelper;
import com.worktracker.dailydeed.models.WorkEntry;
import com.worktracker.dailydeed.utils.PreferenceManager;
import com.worktracker.dailydeed.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewEntriesActivity extends AppCompatActivity implements WorkEntryAdapter.OnWorkEntryClickListener {
    
    private EditText etSearch;
    private Button btnFilterToday, btnFilterWeek, btnFilterAll, btnExport;
    private RecyclerView rvEntries;
    private LinearLayout layoutEmptyState;
    
    private WorkEntryAdapter adapter;
    private DatabaseHelper databaseHelper;
    private PreferenceManager preferenceManager;
    
    private List<WorkEntry> allEntries;
    private List<WorkEntry> filteredEntries;
    private String currentFilter = "all";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entries);
        
        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadEntries();
    }
    
    private void initializeViews() {
        etSearch = findViewById(R.id.etSearch);
        btnFilterToday = findViewById(R.id.btnFilterToday);
        btnFilterWeek = findViewById(R.id.btnFilterWeek);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnExport = findViewById(R.id.btnExport);
        rvEntries = findViewById(R.id.rvEntries);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
    }
    
    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        preferenceManager = new PreferenceManager(this);
    }
    
    private void setupRecyclerView() {
        filteredEntries = new ArrayList<>();
        adapter = new WorkEntryAdapter(this, filteredEntries);
        adapter.setOnWorkEntryClickListener(this);
        
        rvEntries.setLayoutManager(new LinearLayoutManager(this));
        rvEntries.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEntries();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        btnFilterToday.setOnClickListener(v -> {
            currentFilter = "today";
            updateFilterButtons();
            filterEntries();
        });
        
        btnFilterWeek.setOnClickListener(v -> {
            currentFilter = "week";
            updateFilterButtons();
            filterEntries();
        });
        
        btnFilterAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterButtons();
            filterEntries();
        });
        
        btnExport.setOnClickListener(v -> exportEntries());
    }
    
    private void loadEntries() {
        String currentProfile = preferenceManager.getCurrentProfile();
        allEntries = databaseHelper.getAllWorkEntries(currentProfile);
        filterEntries();
    }
    
    private void filterEntries() {
        List<WorkEntry> tempList = new ArrayList<>(allEntries);
        
        // Apply time filter
        switch (currentFilter) {
            case "today":
                tempList = tempList.stream()
                        .filter(entry -> TimeUtils.isToday(entry.getTimestamp()))
                        .collect(Collectors.toList());
                break;
            case "week":
                tempList = tempList.stream()
                        .filter(entry -> TimeUtils.isThisWeek(entry.getTimestamp()))
                        .collect(Collectors.toList());
                break;
            // "all" doesn't need filtering
        }
        
        // Apply search filter
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();
        if (!searchQuery.isEmpty()) {
            tempList = tempList.stream()
                    .filter(entry -> 
                            entry.getLocation().toLowerCase().contains(searchQuery) ||
                            entry.getTaskDescription().toLowerCase().contains(searchQuery) ||
                            (entry.getNotes() != null && entry.getNotes().toLowerCase().contains(searchQuery)))
                    .collect(Collectors.toList());
        }
        
        filteredEntries.clear();
        filteredEntries.addAll(tempList);
        adapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (filteredEntries.isEmpty()) {
            rvEntries.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvEntries.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
    
    private void updateFilterButtons() {
        // Reset all buttons
        btnFilterToday.setSelected(false);
        btnFilterWeek.setSelected(false);
        btnFilterAll.setSelected(false);
        
        // Set selected button
        switch (currentFilter) {
            case "today":
                btnFilterToday.setSelected(true);
                break;
            case "week":
                btnFilterWeek.setSelected(true);
                break;
            case "all":
                btnFilterAll.setSelected(true);
                break;
        }
    }
    
    private void exportEntries() {
        if (filteredEntries.isEmpty()) {
            Toast.makeText(this, "No entries to export", Toast.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder exportData = new StringBuilder();
        exportData.append("Work Entries Export\n");
        exportData.append("===================\n\n");
        
        for (WorkEntry entry : filteredEntries) {
            exportData.append("Date: ").append(entry.getFormattedDate()).append("\n");
            exportData.append("Time: ").append(entry.getTime()).append("\n");
            exportData.append("Location: ").append(entry.getLocation()).append("\n");
            exportData.append("Duration: ").append(entry.getFormattedDuration()).append("\n");
            exportData.append("Task: ").append(entry.getTaskDescription()).append("\n");
            if (entry.hasNotes()) {
                exportData.append("Notes: ").append(entry.getNotes()).append("\n");
            }
            exportData.append("\n---\n\n");
        }
        
        // For now, just show the export data in a dialog
        // In a real app, you'd save to file or share via intent
        new AlertDialog.Builder(this)
                .setTitle("Export Data")
                .setMessage("Export functionality coming soon!\n\nTotal entries: " + filteredEntries.size())
                .setPositiveButton("OK", null)
                .show();
        
        Toast.makeText(this, "Export feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onEditClick(WorkEntry entry) {
        Intent intent = new Intent(this, WorkEntryActivity.class);
        intent.putExtra("entry_id", entry.getId());
        startActivity(intent);
    }
    
    @Override
    public void onDeleteClick(WorkEntry entry) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this work entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteWorkEntry(entry.getId());
                    loadEntries(); // Reload the list
                    Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadEntries(); // Refresh the list when returning from edit
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}