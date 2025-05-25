#!/bin/bash

# Daily Deed Work Tracker - Build Script
# This script builds the Android application

echo "🚀 Building Daily Deed Work Tracker..."
echo "========================================"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

# Build release APK
echo "📦 Building release APK..."
./gradlew assembleRelease

# Show build results
echo ""
echo "✅ Build completed!"
echo "==================="

if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    DEBUG_SIZE=$(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)
    echo "📱 Debug APK: app/build/outputs/apk/debug/app-debug.apk ($DEBUG_SIZE)"
fi

if [ -f "app/build/outputs/apk/release/app-release-unsigned.apk" ]; then
    RELEASE_SIZE=$(du -h app/build/outputs/apk/release/app-release-unsigned.apk | cut -f1)
    echo "📱 Release APK: app/build/outputs/apk/release/app-release-unsigned.apk ($RELEASE_SIZE)"
fi

echo ""
echo "🎯 Ready for installation on Android devices!"
echo "💡 Enable 'Unknown Sources' in Android settings before installing"