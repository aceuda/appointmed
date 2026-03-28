# AppointMed Mobile

This folder contains a Kotlin Android application that integrates with the backend API in the parent project.

## Features
- Register (Patient/Doctor)
- Login
- Dashboard
- Profile view
- Update profile
- Change password
- Retrofit + Gson API integration
- Authorization header support
- Network error handling
- Loading indicators and feedback messages

## Backend URL
The app uses the emulator-friendly API host:

`http://10.0.2.2:8080/api/users/`

If your backend runs on a different host or port, update `ApiClient.kt`.

## Running
1. Open `mobile/` in Android Studio.
2. Sync Gradle.
3. Run the app on an emulator or device.

## Notes
- The Android app stores the current user and token in `SharedPreferences`.
- Backend login/register responses use the existing Spring Boot user endpoints:
  - `POST /api/users/login`
  - `POST /api/users/register`
  - `PUT /api/users/{id}`
