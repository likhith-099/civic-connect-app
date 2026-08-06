# Civic Connect Mobile (Android)

An offline-first, modern Android mobile application built for citizens to report local civic issues (like road damage, water leakage, electricity failure, and garbage accumulation) directly to municipal authorities. Powered by Kotlin, Jetpack Compose, and local SQLite caching for seamless offline reporting and background syncing.

---

## 📱 Features

- **Citizen Reporting Hub**: Quickly submit issues with titles, descriptions, categories, severity metrics, and exact geo-coordinates.
- **Visual Evidence & AI Classifier**: 
  - Capture photos directly via camera or upload from gallery.
  - Integrates with the backend's Azure Computer Vision & Gemini AI pipeline to automatically suggest issue categories and generate professional description summaries.
- **Interactive Community Feed**: Browse local civic issues reported by other citizens, view their resolution statuses (Pending, In Progress, Resolved, Rejected), and upvote/support issues to signal urgency.
- **Offline-First Synchronization**:
  - Fully functional offline. Reports created without network coverage are stored in a local SQLite database (Room).
  - Integrates Jetpack WorkManager to automatically batch and sync queued reports to the cloud backend once network connectivity is restored.
- **Interactive Location Mapping**: Tapping the location card on any complaint details view constructs standard Geo URIs to open coordinates in native mapping applications (like Google Maps or OSM).
- **Citizen Profiles**: Manage account credentials and specify Municipal Area/Region information to customize local reporting workflows.

---

## 🛠️ Tech Stack

- **UI Framework**: Jetpack Compose (Modern declarative UI)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Asynchronous & Flow**: Kotlin Coroutines + StateFlow / SharedFlow
- **Dependency Injection**: Dagger Hilt
- **Local Database**: Room DB (SQLite wrapper with Offline Sync support)
- **Background Task Scheduling**: WorkManager (for robust offline data syncing)
- **Networking**: Retrofit 2 + OkHttp 3 (with custom Authorization interceptors)
- **Image Loading**: Coil (Compose-first asynchronous image loader)

---

## 📂 Project Structure

```text
app/src/main/java/com/civicconnect/
│
├── data/                         # Data layer (DTOs, Repositories, Local DB, API Service)
│   ├── dto/                      # Network Data Transfer Objects (Auth, Complaint, Admin)
│   ├── local/                    # Room Database, DAOs, Entities, and Token Store
│   ├── remote/                   # Retrofit API Interfaces (ComplaintApi, AuthApi, AiApi)
│   └── repository/               # Repository implementations (caching, sync, network)
│
├── domain/                       # Domain layer (pure business logic, model definitions)
│   ├── model/                    # App Models (Complaint, User, TimelineEvent)
│   └── usecase/                  # Single-purpose business Use Cases (Auth, AI, Complaints)
│
└── presentation/                 # Presentation layer (Compose UIs, ViewModels, Themes)
    ├── navigation/               # NavHost, Screen routes, and Bottom Navigation configurations
    ├── screens/                  # Feature Screens (Home, Community, Report Form, Profile, Details)
    └── theme/                    # Material 3 Color palettes, Typography, and Shapes
```

---

## 🚀 Setup & Installation

### Prerequisites
- **Android Studio** (Koala or newer recommended)
- **Android SDK** 26+ (Targeting SDK 34)
- **Java Development Kit (JDK)** 17 (embedded in Android Studio)
- Running instance of the **Civic Connect Backend API**

### 1) Configuration
Create a `local.properties` file in the project root if it doesn't exist, and point it to your Android SDK location:
```properties
sdk.dir=/path/to/your/Android/Sdk
```

In `com/civicconnect/utils/Constants.kt`, verify that `BASE_URL` points to your backend instance:
```kotlin
object Constants {
    // Replace with staging/production Azure URL or local emulator IP (10.0.2.2)
    const val BASE_URL = "https://civic-backend-likhith-b3cddaa9d7bcf9e8.centralindia-01.azurewebsites.net/"
}
```

### 2) Build and Run
1. Open the project in Android Studio.
2. Sync the project with Gradle files.
3. Run the application on an Android Emulator or physical device by clicking the **Run** button (green play icon).
4. Run Kotlin compiler verification tasks via CLI:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🤝 Contributing

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
