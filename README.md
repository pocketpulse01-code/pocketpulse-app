# PocketPulse

PocketPulse includes the Android app source and the public static website and admin status page.

## Live links

- Website: https://pocketpulse01-code.github.io/pocketpulse-app/
- Public admin portal: https://pocketpulse01-code.github.io/pocketpulse-app/admin.html

The admin page is a public, read-only pre-launch status page; it contains no private user records. The app has not been released on Google Play, so the reported download count is 0. Live account and usage metrics are not connected.

## Android app

The Android project is in `android/` and uses package ID `com.pocketpulse.app`.

1. Install Android Studio and open the `android/` directory.
2. Add your Firebase Android configuration at `android/app/google-services.json`. Keep it private; this repository ignores it.
3. Review and deploy `android/firestore.rules` to the matching Firebase project.
4. Build in Android Studio, or run `gradlew.bat assembleDebug` from `android/` on Windows.

This repository does not include signing keys, local SDK paths, build outputs, or Firebase configuration credentials.
