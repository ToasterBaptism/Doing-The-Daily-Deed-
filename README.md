# Daily Deed Work Tracker

A comprehensive Android application for recording and documenting work activities, time management, and task tracking. This app provides a simple and minimal interface for tracking when you show up at work, where you go, what tasks you're doing, and for how long.

## Features

### Core Functionality
- **Work Entry Recording**: Log time, location, task description, duration, and additional notes
- **Profile Management**: Create, load, and manage multiple work profiles
- **Data Persistence**: SQLite database for reliable local storage
- **Search & Filter**: Find entries by location, task, or notes with time-based filtering
- **Real-time Statistics**: View today's entries and total hours worked

### User Interface
- **Material Design 3**: Modern, clean interface following Google's design guidelines
- **Intuitive Navigation**: Simple button-based navigation between activities
- **Responsive Layout**: Optimized for various Android screen sizes
- **Dark/Light Theme Support**: Automatic theme adaptation

### Data Management
- **SQLite Database**: Robust local data storage
- **Profile System**: Separate work profiles for different jobs or projects
- **Backup Ready**: Configured for Android's auto-backup system
- **Export Capability**: Framework for data export (extensible)

## Technical Specifications

### Development Environment
- **Language**: Java
- **Platform**: Android SDK (API 21-34)
- **Build System**: Gradle 8.0
- **Architecture**: MVC pattern with SQLite database

### Dependencies
- `androidx.appcompat:appcompat:1.6.1` - Backward compatibility
- `com.google.android.material:material:1.10.0` - Material Design components
- `androidx.constraintlayout:constraintlayout:2.1.4` - Flexible layouts
- `androidx.recyclerview:recyclerview:1.3.2` - List displays
- `com.google.code.gson:gson:2.10.1` - JSON serialization

### Minimum Requirements
- **Android Version**: 5.0 (API level 21)
- **Target Version**: Android 14 (API level 34)
- **Storage**: ~10MB app size
- **Permissions**: None required (location features planned for future)

## Project Structure

```
DailyDeedWorkTracker/
├── app/
│   ├── src/main/
│   │   ├── java/com/worktracker/dailydeed/
│   │   │   ├── MainActivity.java              # Main dashboard
│   │   │   ├── WorkEntryActivity.java         # Add/edit work entries
│   │   │   ├── ViewEntriesActivity.java       # View and manage entries
│   │   │   ├── ProfileManagerActivity.java    # Profile management
│   │   │   ├── SettingsActivity.java          # App settings
│   │   │   ├── models/
│   │   │   │   ├── WorkEntry.java             # Work entry data model
│   │   │   │   └── Profile.java               # Profile data model
│   │   │   ├── database/
│   │   │   │   └── DatabaseHelper.java        # SQLite database operations
│   │   │   ├── adapters/
│   │   │   │   ├── WorkEntryAdapter.java      # RecyclerView adapter for entries
│   │   │   │   └── ProfileAdapter.java        # RecyclerView adapter for profiles
│   │   │   └── utils/
│   │   │       ├── TimeUtils.java             # Time formatting utilities
│   │   │       └── PreferenceManager.java     # Shared preferences manager
│   │   └── res/
│   │       ├── layout/                        # XML layout files
│   │       ├── values/                        # Strings, colors, themes
│   │       ├── drawable/                      # Custom drawables
│   │       ├── mipmap-*/                      # App icons
│   │       └── xml/                           # Backup rules
│   └── build.gradle                           # App-level build configuration
├── build.gradle                               # Project-level build configuration
├── settings.gradle                            # Gradle settings
└── gradle/wrapper/                            # Gradle wrapper files
```

## Installation & Setup

### Prerequisites
1. **Android SDK**: API levels 21-34 installed
2. **Java Development Kit**: JDK 8 or higher
3. **Gradle**: Version 8.0 (included via wrapper)

### Building from Source
```bash
# Clone the repository
git clone <repository-url>
cd DailyDeedWorkTracker

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

### APK Installation
1. Download the APK file from the releases
2. Enable "Install from unknown sources" in Android settings
3. Install the APK file
4. Launch "Daily Deed Work Tracker"

## Usage Guide

### Getting Started
1. **First Launch**: The app creates a "Default" profile automatically
2. **Add Entry**: Tap "New Entry" to record your first work activity
3. **Fill Details**: Enter time, location, task description, and duration
4. **Save**: Your entry is stored locally on your device

### Managing Profiles
1. **Create Profile**: Go to Profile Manager → Enter name → Create Profile
2. **Switch Profile**: Load any profile to switch your active workspace
3. **Delete Profile**: Remove profiles you no longer need (except Default)

### Viewing Entries
1. **All Entries**: View complete history for current profile
2. **Filter Options**: Today, This Week, or All entries
3. **Search**: Find entries by location, task, or notes
4. **Edit/Delete**: Tap buttons on any entry to modify or remove

### Settings
- **Time Format**: Choose between 12-hour and 24-hour display
- **Auto Location**: Enable for future GPS integration
- **Backup/Restore**: Planned features for data management

## Database Schema

### Work Entries Table
```sql
CREATE TABLE work_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    time TEXT NOT NULL,
    location TEXT NOT NULL,
    task_description TEXT NOT NULL,
    duration REAL NOT NULL,
    notes TEXT,
    timestamp INTEGER NOT NULL,
    profile_name TEXT NOT NULL
);
```

### Profiles Table
```sql
CREATE TABLE profiles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    created_timestamp INTEGER NOT NULL,
    is_active INTEGER NOT NULL DEFAULT 0
);
```

## Development Notes

### Architecture Decisions
- **SQLite over Room**: Direct SQLite for simplicity and control
- **Java over Kotlin**: Broader compatibility and familiarity
- **Material Design 3**: Modern UI following Android guidelines
- **MVC Pattern**: Clear separation of concerns

### Future Enhancements
- **GPS Integration**: Automatic location detection
- **Export Features**: CSV, PDF, and cloud export options
- **Time Tracking**: Built-in timer for active work sessions
- **Reporting**: Charts and analytics for work patterns
- **Cloud Sync**: Optional cloud backup and sync
- **Widgets**: Home screen widgets for quick entry

### Known Limitations
- No automatic location detection (manual entry required)
- Export features are placeholder implementations
- No cloud synchronization
- Single-device usage only

## Contributing

### Development Setup
1. Clone the repository
2. Open in Android Studio or your preferred IDE
3. Ensure Android SDK is properly configured
4. Build and run on emulator or device

### Code Style
- Follow standard Java conventions
- Use meaningful variable and method names
- Comment complex logic
- Maintain consistent indentation

### Testing
- Test on multiple Android versions (API 21+)
- Verify database operations
- Test UI responsiveness
- Validate input handling

## License

This project is open source. See LICENSE file for details.

## Support

For issues, feature requests, or questions:
1. Check existing issues in the repository
2. Create a new issue with detailed description
3. Include Android version and device information
4. Provide steps to reproduce any bugs

## Version History

### v1.0.0 (Current)
- Initial release
- Core work entry functionality
- Profile management system
- SQLite database integration
- Material Design 3 UI
- Search and filter capabilities
- Settings management

---

**Daily Deed Work Tracker** - Simple, reliable work activity tracking for Android.