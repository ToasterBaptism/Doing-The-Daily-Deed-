package com.worktracker.dailydeed.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    public static String getCurrentTime12Hour() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    public static String formatTime(String time24Hour, boolean use24HourFormat) {
        if (use24HourFormat) {
            return time24Hour;
        }
        
        try {
            SimpleDateFormat input = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = input.parse(time24Hour);
            return output.format(date);
        } catch (Exception e) {
            return time24Hour; // Return original if parsing fails
        }
    }
    
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    public static String getFormattedDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    public static String getFormattedDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    public static boolean isToday(long timestamp) {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);
        
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
    }
    
    public static boolean isThisWeek(long timestamp) {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);
        
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
               today.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR);
    }
    
    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}