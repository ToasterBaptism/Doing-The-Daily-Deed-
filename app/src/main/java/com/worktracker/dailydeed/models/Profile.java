package com.worktracker.dailydeed.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Profile {
    private long id;
    private String name;
    private long createdTimestamp;
    private boolean isActive;

    public Profile() {
        this.createdTimestamp = System.currentTimeMillis();
        this.isActive = false;
    }

    public Profile(String name) {
        this.name = name;
        this.createdTimestamp = System.currentTimeMillis();
        this.isActive = false;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Utility methods
    public String getFormattedCreatedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(createdTimestamp));
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Profile profile = (Profile) obj;
        return id == profile.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}