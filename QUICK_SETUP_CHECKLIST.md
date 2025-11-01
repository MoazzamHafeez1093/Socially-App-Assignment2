# Quick Setup Checklist for Socially App

## 🚀 Pre-Flight Checklist

Before running the app, ensure all these items are completed:

---

## ✅ 1. Firebase Configuration

### Check Firebase Setup
- [ ] `google-services.json` exists in `app/` directory
- [ ] Firebase Authentication is enabled for Email/Password
- [ ] Firebase Realtime Database is created
- [ ] Database rules allow authenticated read/write

### Firebase Database Rules (Example)
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null",
    "users": {
      "$uid": {
        ".read": true,
        ".write": "$uid === auth.uid"
      }
    },
    "stories": {
      ".read": true,
      ".write": "auth != null"
    },
    "posts": {
      ".read": true,
      ".write": "auth != null"
    },
    "messages": {
      "$chatId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    }
  }
}
```

---

## ✅ 2. Layout Files

### Required Layout Updates

#### activity_home_screen.xml
- [ ] Add `storiesRecyclerView` (see LAYOUT_UPDATES_REQUIRED.md)
- [ ] RecyclerView is set to horizontal orientation
- [ ] ID is exactly `android:id="@+id/storiesRecyclerView"`

#### activity_story_upload.xml (if exists)
- [ ] Has `storyPreview` ImageView
- [ ] Has `selectPhotoButton` Button
- [ ] Has `selectVideoButton` Button
- [ ] Has `uploadStoryButton` Button
- [ ] Has `cancelButton` Button

#### activity_signup.xml (if exists)
- [ ] Has `usernameTextBox` EditText
- [ ] Has `emailTextBox` EditText
- [ ] Has `passwordTextBox` EditText
- [ ] Has `profileImage` ImageView
- [ ] Has `btnSignUp` Button
- [ ] Has `loginBtn` Button

**Note:** If any layout file is missing or has errors, the programmatic fallback will automatically be used.

---

## ✅ 3. Permissions

### AndroidManifest.xml Permissions
All these should already be in your manifest:

- [ ] `INTERNET`
- [ ] `ACCESS_NETWORK_STATE`
- [ ] `CAMERA`
- [ ] `READ_EXTERNAL_STORAGE`
- [ ] `WRITE_EXTERNAL_STORAGE`
- [ ] `READ_MEDIA_IMAGES`
- [ ] `READ_MEDIA_VIDEO`

---

## ✅ 4. Dependencies

### Check build.gradle.kts files have:
- [ ] Firebase BOM (Bill of Materials)
- [ ] Firebase Auth
- [ ] Firebase Database
- [ ] RecyclerView support
- [ ] Material Design components

### Example Dependencies
```kotlin
dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    
    // Android
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.11.0")
}
```

---

## ✅ 5. Code Files

### Verify All Files Exist
- [ ] `signup.kt` - Updated with Firebase Auth
- [ ] `LoginActivity.kt` - Exists and functional
- [ ] `MainActivity.kt` - Splash screen with auth check
- [ ] `HomeScreen.kt` - Updated with story loading
- [ ] `story_Upload.kt` - Updated with image picker
- [ ] `adapters/StoryAdapter.kt` - New file created
- [ ] `models/Story.kt` - Updated with Base64 fields
- [ ] `models/User.kt` - Updated with following/followers
- [ ] `utils/Base64Image.kt` - Exists for image conversion
- [ ] `utils/PostRepository.kt` - Updated with user filtering
- [ ] `utils/FirebaseAuthManager.kt` - Exists for auth
- [ ] `utils/ChatRepository.kt` - Exists for messaging

---

## ✅ 6. Test User Account

### Create Test Account
Before testing, create a test user:

1. [ ] Run the app
2. [ ] Wait for splash screen (5 seconds)
3. [ ] Tap "Sign Up" (or it shows signup automatically)
4. [ ] Enter test credentials:
   - Username: `testuser`
   - Email: `test@example.com`
   - Password: `test123` (min 6 chars)
5. [ ] Optionally select profile picture
6. [ ] Tap "Create Account"
7. [ ] Should navigate to HomeScreen

---

## ✅ 7. Build and Run

### Build Commands
```bash
# Clean previous builds
./gradlew clean

# Build the app
./gradlew build

# Or use Android Studio:
# Build -> Clean Project
# Build -> Rebuild Project
```

### Run the App
- [ ] Connect Android device or start emulator
- [ ] Click "Run" in Android Studio
- [ ] Or use: `./gradlew installDebug`

---

## ✅ 8. Test Core Features

### Test Signup/Login Flow
1. [ ] App opens to splash screen
2. [ ] After 5 seconds, shows Login or Signup screen
3. [ ] Can create new account
4. [ ] Can login with existing account
5. [ ] Session persists (close and reopen app)

### Test Story Feature
1. [ ] HomeScreen shows horizontal story area at top
2. [ ] Can tap profile image to upload story
3. [ ] Image picker opens
4. [ ] Can select photo
5. [ ] Story uploads successfully
6. [ ] Story appears in horizontal scroll
7. [ ] Can tap story to view

### Test Feed
1. [ ] Can create posts (tap + button)
2. [ ] Posts appear in feed
3. [ ] Can like/comment on posts
4. [ ] Feed shows posts from followed users

### Test Search
1. [ ] Can search for users
2. [ ] Can follow/unfollow users
3. [ ] Following list updates

---

## ✅ 9. Common Issues and Quick Fixes

### Issue: Firebase not initialized
**Fix:** Check `google-services.json` is in correct location

### Issue: Layout not found
**Fix:** Programmatic fallback will automatically activate - no action needed

### Issue: Images not displaying
**Fix:** 
- Check internet connection
- Verify Firebase Database rules allow read
- Reduce image quality in Base64Image.kt (already set to 70%)

### Issue: Login not working
**Fix:**
- Check Firebase Auth is enabled
- Verify email format is correct
- Password must be 6+ characters

### Issue: Stories not showing
**Fix:**
- Add `storiesRecyclerView` to layout (see LAYOUT_UPDATES_REQUIRED.md)
- Or fallback will show simple view
- Upload a test story first

---

## ✅ 10. Monitoring and Debugging

### Enable Debug Logging
In Android Studio:
1. [ ] Open Logcat
2. [ ] Filter by app package: `com.example.assignment1`
3. [ ] Look for error messages

### Firebase Console
1. [ ] Check Firebase Console > Authentication for user accounts
2. [ ] Check Database tab to see data structure
3. [ ] Monitor Database usage (free plan limit: 1GB)

### Key Log Tags to Watch
- `MainActivity` - Splash screen and navigation
- `LoginActivity` - Login process
- `signup` - Signup process
- `HomeScreen` - Story loading and feed
- `story_Upload` - Story upload process
- `Base64Image` - Image conversion

---

## 📱 Expected App Flow

```
App Launch
    ↓
Splash Screen (5 seconds)
    ↓
Check Auth State
    ↓
┌──────────────────┬──────────────────┐
│   Logged Out     │    Logged In     │
│        ↓         │        ↓         │
│  Login Screen    │   Home Screen    │
│        ↓         │        ↓         │
│  → Signup        │   [Stories]      │
│        ↓         │        ↓         │
│  Home Screen ←───┴─── [Posts]      │
└──────────────────────────────────────┘
```

---

## 🎉 Ready to Go!

If all checkboxes are complete, you're ready to:
1. Build the app
2. Run on device/emulator
3. Test all features
4. Deploy!

---

## 📚 Documentation References

- **IMPLEMENTATION_SUMMARY.md** - Complete feature documentation
- **LAYOUT_UPDATES_REQUIRED.md** - Detailed layout instructions
- **README.md** - Project overview (if exists)

---

## 🆘 Need Help?

### Quick Debug Steps
1. Check Logcat for errors
2. Verify Firebase connection
3. Test with fresh app install
4. Check all layout IDs match code
5. Ensure internet permissions granted

### All Features Should Work
✅ Signup with email/password
✅ Login with persistence
✅ Upload stories (24-hour expiry)
✅ View stories (horizontal scroll)
✅ Create posts
✅ Like/comment on posts
✅ Follow/unfollow users
✅ User-specific feeds
✅ Chat/messaging
✅ Base64 image storage

---

**Last Updated:** 2025
**Version:** 1.0 - Full Feature Implementation

