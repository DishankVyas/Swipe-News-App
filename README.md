# SwipeNews - Tinder-Style News Aggregation App 📰

A modern Android news app that brings the engaging swipe interaction of Tinder to news browsing. Swipe right to save articles, swipe left to skip them!

## Features ✨

### Core Functionality
- **Tinder-Style Swipe Interface**: Swipe right to save, left to skip
- **Firebase Authentication**: Email/Password and Google Sign-In
- **Real-Time News**: Fetch latest headlines from NewsAPI.org
- **Offline Mode**: Access saved articles without internet
- **Category Filters**: Browse by Technology, Sports, Business, etc.
- **Share Articles**: Share via WhatsApp, Gmail, or any installed app
- **Dark Mode**: Eye-friendly theme toggle

### Technical Highlights
- **100% Java**: No Kotlin - pure Java implementation
- **MVC Architecture**: Clean separation of concerns
- **Firebase Firestore**: Cloud database with offline persistence
- **Retrofit + Gson**: Type-safe REST API integration
- **Material Design**: Modern UI/UX with smooth animations
- **Firestore Security Rules**: User-specific data access control

## Screenshots 📱

```
[Login Screen] → [News Feed] → [Saved Articles] → [Settings]
```

## Tech Stack 🛠️

| Component | Technology |
|-----------|-----------|
| Language | Java |
| Architecture | MVC (Model-View-Controller) |
| Authentication | Firebase Auth |
| Database | Cloud Firestore |
| API Client | Retrofit 2 + Gson |
| Image Loading | Glide |
| Swipe Cards | CardStackView |
| UI Framework | Material Design 3 |
| Location Services | Google Play Services (Optional) |

## Dependencies 📦

```gradle
// Firebase (with BoM for version management)
implementation platform("com.google.firebase:firebase-bom:32.7.0")
implementation "com.google.firebase:firebase-auth"
implementation "com.google.firebase:firebase-firestore"
implementation "com.google.firebase:firebase-messaging"

// Networking
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.retrofit2:converter-gson:2.9.0"

// Image Loading
implementation "com.github.bumptech.glide:glide:4.16.0"

// Card Stack View
implementation "com.github.yuyakaido:CardStackView:2.3.4"

// Material Design
implementation "com.google.android.material:material:1.11.0"
```

## Project Structure 📁

```
app/src/main/java/com/nmims/bigmanting/
├── activities/
│   ├── LoginActivity.java          # Authentication UI
│   ├── MainActivity.java            # News feed with swipe cards
│   ├── SavedActivity.java          # Display saved articles
│   └── SettingsActivity.java       # App settings & preferences
├── adapters/
│   ├── CardAdapter.java            # Card stack adapter
│   └── SavedAdapter.java           # Saved articles adapter
├── helpers/
│   ├── FirestoreHelper.java        # Firestore CRUD operations
│   ├── NewsApiService.java         # Retrofit API interface
│   └── RetrofitClient.java         # Retrofit singleton
├── models/
│   ├── ArticleModel.java           # Article POJO
│   ├── SourceModel.java            # News source model
│   └── NewsResponse.java           # API response model
├── services/
│   └── NewsNotificationService.java # FCM push notifications
├── utils/
│   ├── Constants.java              # App constants & security rules
│   └── SharedPrefsHelper.java      # SharedPreferences wrapper
└── SwipeNewsApplication.java       # Application class (Firestore init)
```

## Setup Instructions 🚀

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11+
- Android device/emulator (API 25+)
- NewsAPI account (free)
- Firebase account (free)

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd bigmanting
   ```

2. **Get NewsAPI Key**
   - Register at [newsapi.org](https://newsapi.org/register)
   - Copy your API key
   - Update `Constants.java`:
     ```java
     public static final String NEWS_API_KEY = "your_key_here";
     ```

3. **Set up Firebase**
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Add Android app with package name: `com.nmims.bigmanting`
   - Download `google-services.json` and place in `app/` directory
   - Enable Authentication (Email/Password and Google)
   - Create Firestore database
   - Add security rules from `FIRESTORE_RULES.txt`

4. **Configure Google Sign-In**
   - Get SHA-1 fingerprint:
     ```bash
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
   - Add SHA-1 to Firebase Console
   - Copy Web Client ID to `strings.xml`

5. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

For detailed setup instructions, see [SETUP_INSTRUCTIONS.md](SETUP_INSTRUCTIONS.md)

## Usage 📖

### Authentication
1. Launch app
2. Register with email/password OR sign in with Google
3. Upon successful login, you'll see the news feed

### Browsing News
1. View news articles displayed as swipeable cards
2. **Swipe right** to save an article
3. **Swipe left** to skip an article
4. Use category chips to filter by topic

### Saved Articles
1. Tap the bookmark FAB on the main screen
2. View all your saved articles
3. Tap an article to open in browser
4. Tap share to share via apps
5. Tap delete to remove from saved

### Settings
1. Open menu → Settings
2. Toggle dark mode
3. Enter custom NewsAPI key (optional)
4. Logout when done

## Firestore Security Rules 🔒

The app uses strict Firestore security rules to ensure data privacy:

```javascript
match /users/{userId}/saved_articles/{article} {
  allow read, write: if request.auth != null && request.auth.uid == userId;
}
```

This ensures users can only access their own saved articles.

## Offline Support 💾

SwipeNews works offline thanks to Firestore's persistence:
- Saved articles are cached locally
- Changes sync automatically when online
- No data loss during offline periods

Enable in `SwipeNewsApplication.java`:
```java
FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .build();
```

## API Integration 🌐

### NewsAPI Endpoints Used:
1. **Top Headlines**: `/v2/top-headlines`
   - Parameters: country, category, apiKey
2. **Everything**: `/v2/everything`
   - Parameters: q, language, sortBy, apiKey

### Categories Supported:
- General
- Technology
- Business
- Sports
- Entertainment
- Health
- Science

## Demo & Presentation 🎤

For a complete demo script with talking points, see [DEMO_SCRIPT.md](DEMO_SCRIPT.md)

## Evaluation Criteria Coverage ✅

| Criteria | Implementation | Weight |
|----------|---------------|--------|
| Design & UI/UX | Material Design, smooth animations | 15% |
| Functionality | Auth, API, swipe, save, share, offline | 20% |
| Innovation | Tinder-style news browsing | 10% |
| Technical Complexity | Multiple tech stack integration | 15% |
| Security | Firebase Auth, Firestore rules | 10% |
| Testing | Offline mode, sync tested | 10% |
| Documentation | Complete guides & comments | 15% |

## Future Enhancements 🔮

- [ ] Location-based trending news
- [ ] Search functionality
- [ ] Article categories for saved items
- [ ] Push notifications for breaking news
- [ ] User preferences for news sources
- [ ] Article summary using ML
- [ ] Social sharing statistics

## Known Limitations ⚠️

1. **NewsAPI Free Tier**: 100 requests/day
2. **Image Loading**: Depends on article source quality
3. **Offline Articles**: Only previously saved articles available offline

## Troubleshooting 🔧

### Common Issues:

**Q: App crashes on launch**
- Verify `google-services.json` is in `app/` directory
- Check Logcat for specific errors

**Q: Google Sign-In fails**
- Add SHA-1 fingerprint to Firebase Console
- Download updated `google-services.json`

**Q: No articles loading**
- Verify NewsAPI key is correct
- Check internet connection
- Check Logcat for API errors

See [SETUP_INSTRUCTIONS.md](SETUP_INSTRUCTIONS.md) for more troubleshooting tips.

## License 📄

This project is for educational purposes as part of a Mobile Application Development course.

## Author 👨‍💻

Built with ❤️ for NMIMS Mobile Application Development project

## Acknowledgments 🙏

- [NewsAPI.org](https://newsapi.org) for news data
- [Firebase](https://firebase.google.com) for backend services
- [CardStackView](https://github.com/yuyakaido/CardStackView) for swipe UI
- [Glide](https://github.com/bumptech/glide) for image loading

---

**Ready to swipe through the news?** 🚀📱
