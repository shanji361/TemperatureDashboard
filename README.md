# Temperature Dashboard

Temperature Dashboard is an Android app built with Jetpack Compose that simulates real-time temperature updates and visualizes them in an interactive dashboard. It uses coroutines to generate temperature data periodically and StateFlow for reactive UI updates.

---

## Features

- Simulates new temperature readings every **2 seconds** using **Kotlin coroutines**
- Random temperature values between **65°F and 85°F**
- Stores the **last 20 readings** in memory and updates the UI automatically
- Displays:
  - List of readings with **timestamp** and **value**
  - **Current**, **average**, **minimum**, and **maximum** temperature statistics
- **Chart visualization** using a Compose `Canvas` 
- Ability to **pause and resume** data generation
- Reactive data flow implemented with **ViewModel** and **StateFlow**

---

## How to Run the App

1. Clone this repository:
   ```bash
   git clone https://github.com/shanji361/TemperatureDashboard.git
   ```
2. Open the project in Android Studio.

3. Run the app on an emulator or a physical Android device.   
---

## Reference
- Based on **Lecture 4, Example 2** from the CS501 course materials.  
- [Kotlin Documentation on StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)
- [Android Developers Documentation on ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Android Developers Documentation on Canvas](https://developer.android.com/reference/android/graphics/Canvas)
