package com.worktracker.dailydeed.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkEntry {
    private long id;
    private String time;
    private String location;
    private String taskDescription;
    private double duration;
    private String notes;
    private long timestamp;
    private String profileName;

    public WorkEntry() {
        this.timestamp = System.currentTimeMillis();
    }

    public WorkEntry(String time, String location, String taskDescription, double duration, String notes, String profileName) {
        this.time = time;
        this.location = location;
        this.taskDescription = taskDescription;
        this.duration = duration;
        this.notes = notes;
        this.profileName = profileName;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    // Utility methods
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getFormattedDuration() {
        if (duration == 0) return "0 hrs";
        if (duration == 1) return "1 hr";
        return String.format(Locale.getDefault(), "%.1f hrs", duration);
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "WorkEntry{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", duration=" + duration +
                ", notes='" + notes + '\'' +
                ", timestamp=" + timestamp +
                ", profileName='" + profileName + '\'' +
                '}';
    }
}