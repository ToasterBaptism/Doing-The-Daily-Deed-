package com.worktracker.dailydeed.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.worktracker.dailydeed.models.Profile;
import com.worktracker.dailydeed.models.WorkEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "work_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Work Entries Table
    private static final String TABLE_WORK_ENTRIES = "work_entries";
    private static final String COLUMN_ENTRY_ID = "id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_TASK_DESCRIPTION = "task_description";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_PROFILE_NAME = "profile_name";

    // Profiles Table
    private static final String TABLE_PROFILES = "profiles";
    private static final String COLUMN_PROFILE_ID = "id";
    private static final String COLUMN_PROFILE_NAME_UNIQUE = "name";
    private static final String COLUMN_CREATED_TIMESTAMP = "created_timestamp";
    private static final String COLUMN_IS_ACTIVE = "is_active";

    // Create table statements
    private static final String CREATE_WORK_ENTRIES_TABLE = "CREATE TABLE " + TABLE_WORK_ENTRIES + "("
            + COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TIME + " TEXT NOT NULL,"
            + COLUMN_LOCATION + " TEXT NOT NULL,"
            + COLUMN_TASK_DESCRIPTION + " TEXT NOT NULL,"
            + COLUMN_DURATION + " REAL NOT NULL,"
            + COLUMN_NOTES + " TEXT,"
            + COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
            + COLUMN_PROFILE_NAME + " TEXT NOT NULL"
            + ")";

    private static final String CREATE_PROFILES_TABLE = "CREATE TABLE " + TABLE_PROFILES + "("
            + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PROFILE_NAME_UNIQUE + " TEXT UNIQUE NOT NULL,"
            + COLUMN_CREATED_TIMESTAMP + " INTEGER NOT NULL,"
            + COLUMN_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 0"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORK_ENTRIES_TABLE);
        db.execSQL(CREATE_PROFILES_TABLE);
        
        // Create default profile
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_NAME_UNIQUE, "Default");
        values.put(COLUMN_CREATED_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_IS_ACTIVE, 1);
        db.insert(TABLE_PROFILES, null, values);
        
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK_ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        onCreate(db);
    }

    // Work Entry CRUD operations
    public long addWorkEntry(WorkEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_TIME, entry.getTime());
        values.put(COLUMN_LOCATION, entry.getLocation());
        values.put(COLUMN_TASK_DESCRIPTION, entry.getTaskDescription());
        values.put(COLUMN_DURATION, entry.getDuration());
        values.put(COLUMN_NOTES, entry.getNotes());
        values.put(COLUMN_TIMESTAMP, entry.getTimestamp());
        values.put(COLUMN_PROFILE_NAME, entry.getProfileName());
        
        long id = db.insert(TABLE_WORK_ENTRIES, null, values);
        db.close();
        
        Log.d(TAG, "Work entry added with ID: " + id);
        return id;
    }

    public WorkEntry getWorkEntry(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORK_ENTRIES, null, COLUMN_ENTRY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        
        WorkEntry entry = null;
        if (cursor != null && cursor.moveToFirst()) {
            entry = cursorToWorkEntry(cursor);
            cursor.close();
        }
        db.close();
        return entry;
    }

    public List<WorkEntry> getAllWorkEntries(String profileName) {
        List<WorkEntry> entries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_WORK_ENTRIES + 
                           " WHERE " + COLUMN_PROFILE_NAME + "=?" +
                           " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{profileName});
        
        if (cursor.moveToFirst()) {
            do {
                entries.add(cursorToWorkEntry(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public List<WorkEntry> getTodayWorkEntries(String profileName) {
        List<WorkEntry> entries = new ArrayList<>();
        
        // Get start and end of today
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = calendar.getTimeInMillis();
        
        String selectQuery = "SELECT * FROM " + TABLE_WORK_ENTRIES + 
                           " WHERE " + COLUMN_PROFILE_NAME + "=?" +
                           " AND " + COLUMN_TIMESTAMP + " >= ? AND " + COLUMN_TIMESTAMP + " < ?" +
                           " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{profileName, 
                String.valueOf(startOfDay), String.valueOf(endOfDay)});
        
        if (cursor.moveToFirst()) {
            do {
                entries.add(cursorToWorkEntry(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public int updateWorkEntry(WorkEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_TIME, entry.getTime());
        values.put(COLUMN_LOCATION, entry.getLocation());
        values.put(COLUMN_TASK_DESCRIPTION, entry.getTaskDescription());
        values.put(COLUMN_DURATION, entry.getDuration());
        values.put(COLUMN_NOTES, entry.getNotes());
        values.put(COLUMN_PROFILE_NAME, entry.getProfileName());
        
        int rowsAffected = db.update(TABLE_WORK_ENTRIES, values, COLUMN_ENTRY_ID + "=?",
                new String[]{String.valueOf(entry.getId())});
        db.close();
        
        Log.d(TAG, "Work entry updated, rows affected: " + rowsAffected);
        return rowsAffected;
    }

    public void deleteWorkEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORK_ENTRIES, COLUMN_ENTRY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        Log.d(TAG, "Work entry deleted with ID: " + id);
    }

    // Profile CRUD operations
    public long addProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_PROFILE_NAME_UNIQUE, profile.getName());
        values.put(COLUMN_CREATED_TIMESTAMP, profile.getCreatedTimestamp());
        values.put(COLUMN_IS_ACTIVE, profile.isActive() ? 1 : 0);
        
        long id = db.insert(TABLE_PROFILES, null, values);
        db.close();
        
        Log.d(TAG, "Profile added with ID: " + id);
        return id;
    }

    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PROFILES + 
                           " ORDER BY " + COLUMN_CREATED_TIMESTAMP + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                profiles.add(cursorToProfile(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return profiles;
    }

    public Profile getActiveProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROFILES, null, COLUMN_IS_ACTIVE + "=?",
                new String[]{"1"}, null, null, null);
        
        Profile profile = null;
        if (cursor != null && cursor.moveToFirst()) {
            profile = cursorToProfile(cursor);
            cursor.close();
        }
        db.close();
        return profile;
    }

    public void setActiveProfile(String profileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Deactivate all profiles
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_ACTIVE, 0);
        db.update(TABLE_PROFILES, values, null, null);
        
        // Activate the selected profile
        values.put(COLUMN_IS_ACTIVE, 1);
        db.update(TABLE_PROFILES, values, COLUMN_PROFILE_NAME_UNIQUE + "=?", 
                new String[]{profileName});
        
        db.close();
        Log.d(TAG, "Active profile set to: " + profileName);
    }

    public void deleteProfile(String profileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Delete all work entries for this profile
        db.delete(TABLE_WORK_ENTRIES, COLUMN_PROFILE_NAME + "=?", new String[]{profileName});
        
        // Delete the profile
        db.delete(TABLE_PROFILES, COLUMN_PROFILE_NAME_UNIQUE + "=?", new String[]{profileName});
        
        db.close();
        Log.d(TAG, "Profile deleted: " + profileName);
    }

    public int getEntryCountForProfile(String profileName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORK_ENTRIES + 
                                  " WHERE " + COLUMN_PROFILE_NAME + "=?", 
                                  new String[]{profileName});
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public double getTotalHoursForProfile(String profileName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_DURATION + ") FROM " + TABLE_WORK_ENTRIES + 
                                  " WHERE " + COLUMN_PROFILE_NAME + "=?", 
                                  new String[]{profileName});
        
        double totalHours = 0;
        if (cursor.moveToFirst()) {
            totalHours = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return totalHours;
    }

    public double getTodayHoursForProfile(String profileName) {
        // Get start and end of today
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = calendar.getTimeInMillis();
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_DURATION + ") FROM " + TABLE_WORK_ENTRIES + 
                                  " WHERE " + COLUMN_PROFILE_NAME + "=?" +
                                  " AND " + COLUMN_TIMESTAMP + " >= ? AND " + COLUMN_TIMESTAMP + " < ?", 
                                  new String[]{profileName, String.valueOf(startOfDay), String.valueOf(endOfDay)});
        
        double totalHours = 0;
        if (cursor.moveToFirst()) {
            totalHours = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return totalHours;
    }

    // Helper methods
    private WorkEntry cursorToWorkEntry(Cursor cursor) {
        WorkEntry entry = new WorkEntry();
        entry.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ENTRY_ID)));
        entry.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
        entry.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
        entry.setTaskDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DESCRIPTION)));
        entry.setDuration(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
        entry.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
        entry.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
        entry.setProfileName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_NAME)));
        return entry;
    }

    private Profile cursorToProfile(Cursor cursor) {
        Profile profile = new Profile();
        profile.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_ID)));
        profile.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_NAME_UNIQUE)));
        profile.setCreatedTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_TIMESTAMP)));
        profile.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1);
        return profile;
    }
}