# SyncShare

SyncShare is an Android social app for sharing pet-inspired findings and moments. Users can register, log in, create posts with text and images, browse a shared feed, manage their own posts, edit their profile, and view animal facts from an external REST API.

## Features

- Firebase Authentication registration and login
- Persistent login with automatic routing on app restart
- Logout from the profile screen
- Post creation with text and image upload
- Shared social feed where users can view posts from other users
- My Posts screen for editing and deleting personal posts
- Profile management with display name and profile image editing
- External animal facts API integration
- Offline caching with Room for posts, profiles, API data, and local image file paths
- Glide image loading with local file preference and remote URL fallback
- Material Design based UI with fragments and Navigation Component

## Architecture

The app follows MVVM with a repository layer:

- Fragments handle UI rendering and user events.
- ViewModels expose LiveData state and launch asynchronous work.
- Repositories coordinate Firebase, Retrofit, Room, and internal image storage.
- Room stores cached objects and local image file paths.
- Firebase stores remote posts, profiles, authentication data, and uploaded images.

## Tech Stack

- Kotlin
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Room SQLite
- Retrofit + Gson
- Glide
- AndroidX Navigation + SafeArgs
- Material Components
- Coroutines + LiveData

## Screens

- Feed
- Create Post
- My Posts
- Profile
- API Screen
- Login
- Register
- Splash

## How to Run

1. Clone the repository:

   ```bash
   git clone https://github.com/Shaharmayster/Shahar_Ofek_FindMe.git
   ```

2. Open the project in Android Studio.

3. Add the Firebase configuration file:

   ```text
   app/google-services.json
   ```

4. Sync Gradle.

5. Run the app on an emulator or Android device.

## Notes

- The UI is designed with Material Design components.
- Network and Firebase operations are asynchronous.
- Room is used for local object caching and local image path persistence.
- Selected post and profile images are copied into internal app storage before upload, then loaded locally when available.
