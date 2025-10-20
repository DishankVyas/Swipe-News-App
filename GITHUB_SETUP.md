# GitHub Setup Guide

This guide will help you set up this project after cloning from GitHub.

## Prerequisites

- Android Studio (latest version recommended)
- JDK 11 or higher
- Android SDK with API level 35

## Setup Steps

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd bigmanting
```

### 2. Configure API Keys

The project requires a NewsAPI key to function. Follow these steps:

1. **Get a NewsAPI Key:**
   - Visit [https://newsapi.org/register](https://newsapi.org/register)
   - Sign up for a free account
   - Copy your API key

2. **Create local.properties file:**
   - Copy the example file: `cp local.properties.example local.properties`
   - Or create a new file named `local.properties` in the project root
   
3. **Add your API key:**
   - Open `local.properties`
   - Replace `your_news_api_key_here` with your actual NewsAPI key:
   ```
   NEWS_API_KEY=your_actual_api_key_here
   ```

### 3. Firebase Configuration

This project uses Firebase for authentication and data storage. You'll need to:

1. Create a Firebase project at [https://console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file
4. Place it in the `app/` directory

### 4. Build the Project

1. Open the project in Android Studio
2. Let Gradle sync complete
3. Build the project: `Build > Make Project`
4. Run on an emulator or physical device

## Important Notes

- **Never commit `local.properties`** - This file contains sensitive API keys and is gitignored
- **Never commit `google-services.json`** with production credentials
- The `local.properties.example` file is a template - copy it and fill in your own values

## Troubleshooting

### Build fails with "NEWS_API_KEY not found"
- Make sure you've created `local.properties` in the project root
- Verify that the API key is properly formatted: `NEWS_API_KEY=your_key_here`
- Try cleaning and rebuilding: `Build > Clean Project` then `Build > Rebuild Project`

### Firebase authentication not working
- Ensure `google-services.json` is in the `app/` directory
- Verify your package name matches the one in Firebase Console
- Check that Firebase Authentication is enabled in your Firebase project

## Additional Resources

- [NewsAPI Documentation](https://newsapi.org/docs)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Android Developer Guide](https://developer.android.com/guide)

## Support

For issues or questions, please open an issue on the GitHub repository.
