# SwipeNews - Complete Setup Instructions

## Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android device/emulator with API 25 (Android 7.1) or higher
- NewsAPI.org account (free)
- Firebase account (free)

---

## Step 1: Clone/Open the Project

1. Open Android Studio
2. Navigate to `File > Open` and select the `bigmanting` folder
3. Wait for Gradle sync to complete

---

## Step 2: Get NewsAPI Key

1. Go to [https://newsapi.org/register](https://newsapi.org/register)
2. Create a free account
3. Copy your API key
4. Open `app/src/main/java/com/nmims/bigmanting/utils/Constants.java`
5. Replace `YOUR_NEWS_API_KEY_HERE` with your actual API key:
   ```java
   public static final String NEWS_API_KEY = "your_actual_key_here";
   ```

**Note:** You can also enter the API key later in the Settings screen of the app.

---

## Step 3: Set Up Firebase

### 3.1 Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: `SwipeNews` (or any name you prefer)
4. Disable Google Analytics (optional for this project)
5. Click "Create project"

### 3.2 Add Android App to Firebase

1. In Firebase Console, click the Android icon to add an Android app
2. Enter package name: `com.nmims.bigmanting`
3. Enter app nickname: `SwipeNews`
4. Click "Register app"
5. Download the `google-services.json` file
6. Copy `google-services.json` to your project's `app/` directory

### 3.3 Enable Firebase Authentication

1. In Firebase Console, go to "Authentication" in the left sidebar
2. Click "Get started"
3. Enable "Email/Password" sign-in method
4. Enable "Google" sign-in method
   - Add your project support email
   - Download the Web SDK configuration (you'll need the Web Client ID)

### 3.4 Configure Google Sign-In

1. In Firebase Console, under Authentication > Sign-in method > Google
2. Copy the **Web client ID**
3. Open `app/src/main/res/values/strings.xml`
4. Replace `YOUR_WEB_CLIENT_ID_HERE` with your actual Web Client ID:
   ```xml
   <string name="default_web_client_id">your_actual_web_client_id_here</string>
   ```

### 3.5 Enable Cloud Firestore

1. In Firebase Console, go to "Firestore Database"
2. Click "Create database"
3. Choose "Start in **test mode**" (for development)
4. Select a location (choose closest to you)
5. Click "Enable"

### 3.6 Set Up Firestore Security Rules

1. In Firestore Console, go to "Rules" tab
2. Replace the default rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own saved articles
    match /users/{userId}/saved_articles/{article} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Deny all other access
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

3. Click "Publish"

### 3.7 Enable Firebase Storage (Optional)

1. Go to "Storage" in Firebase Console
2. Click "Get started"
3. Choose "Start in test mode"
4. Click "Done"

### 3.8 Enable Firebase Cloud Messaging (Optional)

This is for push notifications feature:

1. Go to "Cloud Messaging" in Firebase Console
2. Note: Cloud Messaging is automatically enabled with Firebase setup
3. You can send test notifications from the Firebase Console

---

## Step 4: Verify google-services.json

1. Make sure `google-services.json` is in the `app/` directory
2. Open `app/build.gradle.kts` and verify this line exists at the top:
   ```kotlin
   id("com.google.gms.google-services")
   ```
3. Sync Gradle files

---

## Step 5: Configure SHA-1 Fingerprint (For Google Sign-In)

### Get SHA-1 Fingerprint:

#### On Mac/Linux:
```bash
cd android
./gradlew signingReport
```

Or using keytool:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### On Windows:
```cmd
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

### Add SHA-1 to Firebase:

1. Copy the SHA-1 fingerprint
2. Go to Firebase Console > Project Settings
3. Scroll to "Your apps" section
4. Click on your Android app
5. Click "Add fingerprint"
6. Paste the SHA-1 and click "Save"
7. Download the updated `google-services.json` and replace the old one

---

## Step 6: Build and Run

1. Connect your Android device or start an emulator
2. Click the "Run" button (green play icon) in Android Studio
3. Select your device
4. Wait for the app to build and install

---

## Step 7: Test the App

### First Launch:
1. You should see the Login screen
2. Try registering with email/password OR
3. Try signing in with Google

### Main Features to Test:
1. **News Feed:**
   - Swipe left to skip articles
   - Swipe right to save articles
   - Test category filters at the top

2. **Saved Articles:**
   - Tap the bookmark FAB to view saved articles
   - Tap an article to open in browser
   - Tap share button to share via apps
   - Tap delete button to remove from saved

3. **Settings:**
   - Toggle dark mode
   - Enter NewsAPI key if you didn't add it in Constants.java
   - Test logout

4. **Offline Mode:**
   - Save some articles
   - Turn off internet
   - Open Saved Articles - they should still be visible (Firestore offline persistence)
   - Turn on internet - changes will sync

---

## Troubleshooting

### Issue: "Default FirebaseApp is not initialized"
**Solution:** Make sure `google-services.json` is in the `app/` directory and sync Gradle.

### Issue: Google Sign-In fails with error 12500
**Solution:**
- Verify SHA-1 fingerprint is added to Firebase
- Download updated `google-services.json`
- Verify Web Client ID in `strings.xml`

### Issue: NewsAPI returns 401 Unauthorized
**Solution:**
- Verify API key is correct in Constants.java or Settings
- Check if your NewsAPI key is valid at newsapi.org

### Issue: Firestore permission denied
**Solution:**
- Verify security rules are properly set in Firestore Console
- Make sure user is authenticated before accessing Firestore

### Issue: Build fails with dependency errors
**Solution:**
- Sync Gradle files
- Clean and rebuild project
- Invalidate caches and restart Android Studio

### Issue: App crashes on start
**Solution:**
- Check Logcat for error messages
- Verify all dependencies are properly synced
- Ensure `google-services.json` is in the correct location

---

## Project Structure

```
app/
├── src/main/
│   ├── java/com/nmims/bigmanting/
│   │   ├── activities/          # All Activity classes
│   │   ├── adapters/            # RecyclerView adapters
│   │   ├── helpers/             # Firestore, Retrofit helpers
│   │   ├── models/              # Data models (POJOs)
│   │   ├── services/            # FCM notification service
│   │   ├── utils/               # Utility classes
│   │   └── SwipeNewsApplication.java
│   ├── res/
│   │   ├── drawable/            # Icons and drawables
│   │   ├── layout/              # XML layouts
│   │   ├── menu/                # Menu files
│   │   └── values/              # Colors, strings, themes
│   ├── AndroidManifest.xml
│   └── google-services.json     # Firebase config (YOU MUST ADD THIS)
└── build.gradle.kts
```

---

## Important Notes

1. **Free Tier Limits:**
   - NewsAPI Free: 100 requests/day
   - Firebase Free (Spark Plan): Sufficient for development
   - Firestore: 50K reads/day, 20K writes/day

2. **Security:**
   - Never commit `google-services.json` to public repositories
   - Never commit API keys to version control
   - Use Firestore security rules in production

3. **Testing:**
   - Test on physical device for best experience
   - Test offline mode by disabling internet
   - Test Google Sign-In on physical device (emulator may have issues)

---

## Next Steps

1. Customize the app theme in `themes.xml` and `colors.xml`
2. Add more news categories
3. Implement location-based trending news
4. Set up Firebase Cloud Messaging for notifications
5. Add search functionality
6. Improve error handling

---

## Support

For issues:
1. Check Android Studio Logcat
2. Verify all setup steps are completed
3. Check Firebase Console for errors
4. Verify NewsAPI key is valid

---

## Demo Ready Checklist

- [ ] Firebase project created and configured
- [ ] google-services.json added to app/ directory
- [ ] NewsAPI key added to Constants.java or Settings
- [ ] SHA-1 fingerprint added to Firebase
- [ ] Authentication methods enabled in Firebase
- [ ] Firestore database created with security rules
- [ ] App builds without errors
- [ ] Email/Password login works
- [ ] Google Sign-In works
- [ ] News articles load and display
- [ ] Swipe gestures work (left to skip, right to save)
- [ ] Saved articles display correctly
- [ ] Share functionality works
- [ ] Offline mode works
- [ ] Settings work (dark mode, API key)

**You're all set! Good luck with your demo!** 🎉
