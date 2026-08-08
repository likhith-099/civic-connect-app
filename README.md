# Civic Connect Mobile (Android)

An offline-first, modern Android mobile application built for citizens to report local civic issues (such as road damage, water leakage, electricity failure, and garbage accumulation) directly to municipal authorities. Powered by Kotlin, Jetpack Compose, Material 3, and local SQLite caching for seamless offline reporting and background syncing.

---

## 📱 Features

- **Redesigned Modern Material 3 UI**: 
  - Vibrant HSL-tailored color system (Deep Azure, Teal, Royal Indigo, Slate surfaces).
  - Modern elevation cards, gradient action buttons, custom text fields with animated focus states.
  - Custom status badges (*Pending*, *In Progress*, *Resolved*, *Rejected*).
- **Citizen Dashboard & Quick Actions**:
  - Live activity dashboard with time-based greetings ("Good Morning", "Good Afternoon").
  - 2x2 metric stat grid with smooth counter animations.
  - One-tap "Report New Issue" quick action CTA.
- **Visual Evidence & AI Classifier**: 
  - Capture photos directly via camera or upload from gallery.
  - Integrates with backend Azure Computer Vision & Gemini AI pipelines to automatically classify issues and suggest professional descriptions.
- **Interactive Community Feed & Upvoting**:
  - Browse local civic issues reported by other citizens with pill tab indicators and filter chips (*All*, *Pending*, *In Progress*, *Resolved*, *Rejected*).
  - Independent one-tap support upvote buttons on complaint cards with instant vote count updates.
- **Robust Offline-First Synchronization & Fallback**:
  - Fully functional offline. Reports created without network coverage are saved in a local SQLite database (Room).
  - Robust local database fallback ensures complaint details and community lists load seamlessly even when offline or when backend services are unreachable.
  - Integrates Jetpack WorkManager to automatically batch and sync queued reports to the cloud once network connectivity is restored.
- **Interactive Location Mapping**: 
  - Tapping location tiles constructs standard Geo URIs to view exact incident coordinates in native mapping applications (such as Google Maps).
- **Municipal Staff Portal**:
  - Dedicated executive staff portal for municipal administrators to review, analyze, update complaint statuses, and inspect issue analytics.

---

## 🛠️ Tech Stack

- **UI Framework**: Jetpack Compose (Modern declarative UI) + Material 3
- **Architecture**: Clean Architecture + MVVM (Model-View-ViewModel)
- **Asynchronous & Flow**: Kotlin Coroutines + StateFlow
- **Dependency Injection**: Dagger Hilt
- **Local Database**: Room DB (SQLite wrapper with offline sync & fallback support)
- **Background Scheduling**: WorkManager (for offline data sync)
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
│   └── usecase/                  # Business Use Cases (Auth, AI, Complaints, Upvoting)
│
└── presentation/                 # Presentation layer (Compose UIs, ViewModels, Themes)
    ├── components/               # Common CC UI components (Shimmers, Empty States, Offline Bar)
    ├── navigation/               # NavHost, Screen routes, and Bottom Navigation configurations
    ├── screens/                  # Feature Screens (Home, Community, Report Form, Profile, Details, Admin)
    └── theme/                    # Material 3 Color palettes, Typography, and Shapes
```

---

## 🚀 Setup & Installation

### Prerequisites
- **Android Studio** (Ladybug / Koala or newer recommended)
- **Android SDK** 26+ (Targeting SDK 35)
- **Java Development Kit (JDK)** 17 / 21
- Running instance of the **Civic Connect Backend API**

### 1) Configuration
Verify `BASE_URL` in `com/civicconnect/utils/Constants.kt` points to your backend instance:
```kotlin
object Constants {
    const val BASE_URL = "https://civic-backend-likhith-b3cddaa9d7bcf9e8.centralindia-01.azurewebsites.net/"
}
```

### 2) Build and Run
1. Open the project in Android Studio.
2. Sync the project with Gradle files.
3. Run the application on an Android Emulator or physical device by clicking the **Run** button.
4. Or compile via CLI:
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
