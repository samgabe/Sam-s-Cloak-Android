# SamsCloak Android

Android application for SamsCloak job application orchestrator with Share-to-App functionality.

## Features

- **Share Intent Integration**: Share job posting screenshots directly from any app
- **Automatic Upload**: Screenshots are automatically uploaded and processed
- **Job Application Management**: View and manage job applications
- **Retrofit Integration**: Type-safe API communication
- **Kotlin Coroutines**: Async operations with proper lifecycle management

## Setup

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.9+

### Installation

1. Open project in Android Studio
2. Sync Gradle files
3. Update `BASE_URL` in `RetrofitClient.kt` to point to your backend
4. Build and run

### Configuration

Update the API base URL in `network/RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "http://your-backend-url:8000/"
```

For Android Emulator, use `http://10.0.2.2:8000/` to access localhost.

## Usage

### Share Job Posting

1. Take a screenshot of a job posting
2. Tap "Share" from the screenshot notification or gallery
3. Select "SamsCloak" from the share menu
4. The app will automatically upload and process the job posting

### Intent Filter

The app registers an intent filter for image sharing:

```xml
<intent-filter>
    <action android:name="android.intent.action.SEND" />
    <category android:name="android.intent.category.DEFAULT" />
    <data android:mimeType="image/*" />
</intent-filter>
```

## Architecture

```
com.samscloak/
├── network/
│   ├── ApiService.kt          # Retrofit API interface
│   └── RetrofitClient.kt      # Retrofit configuration
├── MainActivity.kt             # Main application screen
└── ShareActivity.kt            # Share intent handler
```

## API Integration

The app communicates with the SamsCloak backend using Retrofit:

- **Authentication**: Bearer token stored in SharedPreferences
- **File Upload**: Multipart form data for screenshots
- **Error Handling**: Proper error responses and user feedback

## Building

Debug build:
```bash
./gradlew assembleDebug
```

Release build:
```bash
./gradlew assembleRelease
```

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```
