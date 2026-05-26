# SyncShare

SyncShare is an Android social app for sharing pet-inspired findings and moments. Users can register, log in, create posts with text and images, browse a shared feed, manage their own posts, edit their profile, and view animal facts from an external REST API.

## Features

- Firebase Authentication registration and login
- Persistent login with automatic routing on app restart
- Logout from the profile screen
- Post creation with text and image upload
- Post categories for Lost, Found, Moment, and Tip content
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
- The feed keeps a local last-sync timestamp and continues showing cached content when refresh fails.

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

## Local Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Shaharmayster/Shahar_Ofek_FindMe.git
   ```

2. Create or open a Firebase project.

3. Add an Android app in Firebase with this package name:

   ```text
   com.example.findme_shahar_ofek
   ```

4. Enable Email/Password sign-in in Firebase Authentication.

5. Download the Firebase Android configuration file and place it here:

   ```text
   app/google-services.json
   ```

   This file is intentionally ignored by git and must stay local.

6. Deploy the checked-in Firebase rules before using a shared backend:

   ```bash
   firebase deploy --only firestore:rules,storage
   ```

7. Open the project in Android Studio and sync Gradle.

## Running Locally

1. Start an Android emulator from Android Studio Device Manager, or use an attached Android device.

2. Confirm that a device is available:

   ```bash
   adb devices
   ```

3. Build, install, and launch the debug app:

   ```bash
   ./gradlew :app:installDebug
   adb shell monkey -p com.example.findme_shahar_ofek -c android.intent.category.LAUNCHER 1
   ```

## Firebase Security

- `firestore.rules` requires authenticated users.
- User profile documents under `users/{uid}` are readable and writable only by that user.
- Posts are readable by authenticated users for the shared feed.
- Post creates, updates, and deletes are restricted to the post owner.
- Post writes are validated for owner id, caption length, category, and immutable create time.
- `storage.rules` restricts profile and post uploads to authenticated owners and only allows image uploads under 5 MB.

See [SECURITY.md](SECURITY.md) for contributor security guidance.

## Academic Compliance Notes

- MVVM is implemented with Fragment UI controllers, ViewModels exposing LiveData, repositories for data operations, and Room DAOs for local SQLite access.
- Navigation uses the Navigation Graph with SafeArgs for screen transitions and post-edit arguments.
- Posts are not only image captions: each post is classified as Lost, Found, Moment, or Tip to support a pet/findings-oriented workflow rather than a generic photo feed.
- Firebase is used as the remote backend, while Room and internal app storage provide local object and image caching.
- Image uploads are validated locally and by Firebase Storage Rules for image MIME type and 5 MB maximum size.
- Room schema changes use an explicit migration from version 4 to 5 for the post category column.

## Notes

- The UI is designed with Material Design components.
- Network and Firebase operations are asynchronous.
- Room is used for local object caching and local image path persistence.
- Selected post and profile images are copied into internal app storage before upload, then loaded locally when available.
