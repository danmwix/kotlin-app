# kotlin-app


markdown
# MaternityManagement App

This is a Kotlin-based Android application for maternity management. The app uses **Supabase** as its backend (PostgreSQL) and is structured using Kotlin + Java packages for modularity.

---

## 📂 Project Structure

- data.remote  
  Contains `SupabaseClient` setup for connecting to the backend.  
  Located under `kotlin+java → data.remote`.

- ui  
  Modular UI code under `kotlin+java → ui`:  
  - auth → `MotherLoginScreen.kt`, `MotherRegisterScreen.kt`, `RoleSelection.kt`  
  - mother→ `MotherDashboardScreen.kt`  
  - navigation → `APPNavHost.kt`  
  - components → reusable UI components

- gradleScripts 
  Contains `build.gradle.kts` (Module: app) with all project dependencies.



## Prerequisites

1. VS Code (or Android Studio if preferred) with Kotlin & Android support.  
2. Android Emulator OR an Android device with developer mode enabled.  
3. Git installed on your system.  
4. Supabase Project URL & Anon Key for the backend (update `SupabaseClient` in `data.remote`).  

---

## Setup Instructions

1. Clone the repository:

```bash
git clone https://github.com/danmwix/kotlin-app.git
cd kotlin-app
````

2. Open the project in VS Code

   Install the recommended extensions if prompted (Kotlin, Android, Gradle).

3. Check the folder structure

    `kotlin+java → com.example.maternitymanagement → data.remote` → SupabaseClient
   `ui → auth, mother, navigation, components`

4. Build the project

```bash
./gradlew build
```

5. **Run the app**

   * On emulator: Launch your virtual device, then:

```bash
./gradlew installDebug
```

* On a connected physical device: Enable USB debugging, then:

```bash
./gradlew installDebug
```

> The app should now appear on your emulator or device.

---

## Notes

Ensure your Supabase URL & Anon Key are correctly set in `data.remote`.
UI screens are modular; you can explore or extend the `auth`, `mother`, `navigation`, and `components` packages.
All dependencies are in `build.gradle.kts (Module: app)`. Gradle will sync automatically on first build.

---

## 📌 Key Files Overview

| Folder        | Key Files                                                             | Purpose                        |
| ------------- | --------------------------------------------------------------------- | ------------------------------ |
| `auth`        | `MotherLoginScreen.kt`, `MotherRegisterScreen.kt`, `RoleSelection.kt` | User authentication screens    |
| `mother`      | `MotherDashboardScreen.kt`                                            | Mother’s main dashboard screen |
| `navigation`  | `APPNavHost.kt`                                                       | App navigation setup           |
| `components`  | -                                                                     | Reusable UI components         |
| `data.remote` | `SupabaseClient`                                                      | Backend API client setup       |

---


---

```

---

