# ⚡ Smart Energy Optimizer

**Smart Energy Optimizer** is a modern Android application designed to help users monitor, manage, and reduce their home energy consumption. By combining real-time data visualization, automated scheduling, and AI-driven insights, the app empowers users to make smarter energy choices and save on electricity bills.

---

## ✨ Features

### 🔐 Secure Authentication
*   **Sign In / Sign Up**: Full registration flow including Name, Email, Phone, and Password.
*   **Multi-User Support**: Data is isolated per account using a simulated persistent database.
*   **User Validation**: Ensures accounts are unique and credentials are correct.
*   **Profile Management**: View detailed account information (Full Name, ID, etc.) within the app.

### 📊 Energy Dashboard
*   **Real-time Stats**: Track daily kWh consumption and estimated monthly costs.
*   **Visual Trends**: Interactive charts showing weekly usage trends and top consuming appliances.
*   **Eco Mode**: A one-tap toggle to simulate energy-saving configurations (reduces estimated usage by 15%).

### 🔌 Appliance Management
*   **Device Registry**: Add, edit, or remove home appliances with specific power ratings (Watts) and usage hours.
*   **Categorization**: Organize devices by type (AC, Fan, Lights, Electronics, etc.) with custom icons.

### 📅 Smart Automation
*   **Device Scheduler**: Create automated ON/OFF routines for any appliance to prevent phantom load and unnecessary usage.
*   **Status Tracking**: Easily toggle automation rules on or off.

### 💡 AI Insights & Reports
*   **AI Energy Expert**: Integrates with **Claude AI** to provide actionable, personalized insights based on your usage patterns.
*   **PDF Export**: Generate a professional **PDF Usage Report** including summaries, device breakdowns, and AI tips to share or save.

---

## 🛠 Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **UI Components**: Material Design 3 (M3)
*   **Networking**: HttpURLConnection (for AI API integration)
*   **Reports**: Android `PdfDocument` API
*   **Animations**: Compose Animation (Transitions, Shimmer effects)

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug or newer.
*   Android SDK 24 (Nougat) or higher.

### Installation
1.  **Clone the repository**:
    ```sh
    git clone https://github.com/Ruthvik-R/Smart_Energy_Optimizer.git
    ```
2.  **Open in Android Studio**:
    *   Wait for Gradle sync to complete.
3.  **AI Setup (Optional)**:
    *   To enable live AI insights, add your Claude API key in `Insightsscreen.kt`:
    ```kotlin
    private const val CLAUDE_API_KEY = "YOUR_API_KEY_HERE"
    ```
4.  **Run the app**:
    *   Select an emulator or physical device and click **Run**.

---

## 📂 Project Structure

```
com.example.smart_energy_optimizer
├── AuthScreens.kt       # Sign In & Sign Up UI logic
├── DashboardScreen.kt   # Usage stats and Charts
├── Devicesscreen.kt     # Appliance CRUD management
├── Insightsscreen.kt    # AI integration & Shimmer effects
├── Schedulerscreen.kt   # Automation & Routines
├── Settingsscreen.kt    # Profile, Privacy & PDF Export
├── EnergyViewModel.kt   # Core business logic & State management
├── PdfExporter.kt       # PDF generation engine
├── MockDatabase.kt      # Simulated persistence layer
├── Models.kt            # Data classes (User, Device, Schedule)
└── Theme.kt             # Material 3 Theme & Custom Styles
```

---

## 🛡️ Privacy Policy
Your data is stored locally in this version of the app. We prioritize user privacy and do not share your appliance data with third parties, except for processing AI insights via encrypted API calls if the feature is enabled.

---

**Developed with ❤️ by [Ruthvik R](mailto:ruthvikr2004@gmail.com)**
*Empowering a greener future through smart technology.*
