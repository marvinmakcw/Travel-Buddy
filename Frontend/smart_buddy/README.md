# Smart Buddy Flutter App

This folder contains the Flutter frontend for the Smart Buddy travel application.

## 📱 MVVM Architecture Structure

```
lib/
├── main.dart                 # App entry point
├── models/                   # Data models
│   ├── api_response.dart    # API response wrapper
│   ├── credential_dto.dart  # Login credentials
│   └── token_dto.dart       # Authentication token
├── screens/                 # UI screens
│   ├── splash_screen.dart   # App splash screen
│   └── ...                  # Other screens
├── viewmodels/              # Business logic and state management
├── services/                # API and external service calls
│   └── api_service.dart     # Backend API communication
├── exceptions/              # Custom exception handling
│   └── auth_exceptions.dart # Authentication exceptions
└── widgets/                 # Reusable UI components

assets/
├── images/                  # Image assets
└── icons/                   # Icon assets (includes app_icon.png)
```

## 🚀 Features

- **Authentication System**: Secure login with JWT tokens
- **Splash Screen**: Animated app launch with custom icon
- **Itinerary Generation**: Auto-generate travel itineraries
- **Weather Integration**: Dynamic weather updates
- **Timetable Management**: Transportation schedules
- **Delay Updates**: Real-time delay notifications
- **Smart Recommendations**: AI-powered suggestions

## 📋 Prerequisites

Before setting up the Flutter app, ensure you have:

- **Flutter SDK** (3.0.0 or higher)
- **Android Studio** (for Android development)
- **Android SDK** (API level 21 or higher)
- **Java Development Kit (JDK)** 11 or higher
- **Spring Boot Backend** running (see Backend README)

## ⚙️ Setup Instructions

### 1. Install Dependencies

```bash
cd Frontend/smart_buddy
flutter pub get
```

### 2. Configure API Endpoint

The app needs to connect to your Spring Boot backend. You need to update the API base URL in the `ApiService` class:

1. Open `lib/services/api_service.dart`
2. Find the `baseUrl` constant (around line 16)
3. Update it with your PC's IP address:

```dart
// Replace 'YOUR_PC_IP' with your actual IP address
static const String baseUrl = 'http://YOUR_PC_IP:8080';
```

#### 🔍 How to Find Your PC's IP Address:

**On Windows:**
```powershell
ipconfig | findstr "IPv4"
```

**On macOS/Linux:**
```bash
ifconfig | grep "inet "
```

Look for an IP address like `192.168.x.x` (your local network IP).

#### 📱 Network Configuration Examples:

```dart
// For Android Emulator (if backend is on same machine)
static const String baseUrl = 'http://10.0.2.2:8080';

// For Physical Device or different network setup
static const String baseUrl = 'http://192.168.1.100:8080';  // Replace with your PC's IP

// For localhost testing (web/desktop only)
static const String baseUrl = 'http://localhost:8080';
```

### 3. Generate App Icons (Optional)

If you want to update the app icon:

1. Replace `assets/icons/app_icon.png` with your custom icon (1024x1024 recommended)
2. Run the icon generator:

```bash
flutter pub run flutter_launcher_icons
```

### 4. Run the Application

#### For Android Emulator:
```bash
# Start an Android emulator first, then:
flutter run
```


## 🛠️ Development Workflow

### Hot Reload
During development, you can use Flutter's hot reload feature:
- Press `r` in the terminal to hot reload
- Press `R` for hot restart
- Press `q` to quit

### Build for Release
```bash
# Build APK
flutter build apk --release

# Build App Bundle (recommended for Play Store)
flutter build appbundle --release
```

## 🔧 Troubleshooting

### Common Issues:

1. **"No internet connection" error:**
   - Ensure your Spring Boot backend is running on port 8080
   - Verify the IP address in `api_service.dart` is correct
   - Check if Windows Firewall is blocking the connection

2. **Build failures with spaces in path:**
   - Ensure your project path doesn't contain spaces
   - If it does, consider moving the project to a path without spaces

3. **Android build errors:**
   - Run `flutter clean` and then `flutter pub get`
   - Ensure Android SDK is properly installed
   - Check that `android/local.properties` has correct SDK path

4. **Emulator not connecting to localhost:**
   - Use `10.0.2.2:8080` instead of `localhost:8080` for Android emulator
   - For physical devices, use your PC's actual IP address

### Debug Commands:
```bash
# Check Flutter installation
flutter doctor

# List available devices
flutter devices

# Run with verbose logging
flutter run -v

# Clean build files
flutter clean
```

## 📁 Project Structure Details

### Key Files:
- **`lib/main.dart`**: App entry point and routing setup
- **`lib/services/api_service.dart`**: Backend API communication
- **`lib/screens/splash_screen.dart`**: Animated splash screen with app icon
- **`pubspec.yaml`**: Dependencies and asset configuration
- **`android/app/build.gradle.kts`**: Android-specific build configuration

### Important Configurations:
- **Minimum SDK**: Android API level 21 (Android 5.0)
- **Target Platform**: Android only (iOS folders removed for this project)
- **Authentication**: JWT token-based authentication
- **State Management**: Provider pattern (can be extended)

## 🔗 Backend Integration

This frontend connects to the Smart Buddy Spring Boot backend. Ensure the backend is running on port 8080 before testing the app.

**Backend Endpoints Used:**
- `POST /smart_buddy/auth/tokens` - User authentication

For backend setup instructions, see the Backend README file.

## 📝 Development Notes

- The app uses MVVM architecture for clean separation of concerns
- Custom exceptions provide specific error handling for different scenarios
- Splash screen includes smooth animations and your custom app icon
- API service includes error handling for network issues and server responses
- The project is configured for Android-only deployment (other platform folders removed)
