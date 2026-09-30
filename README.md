# PocketPulse

PocketPulse includes the Android app source and the public static website and admin status page.

## Website and admin portal

The repository root contains the website (`index.html`) and the admin status page (`admin.html`). The admin page is a public, read-only pre-launch status page; it contains no private user records. App download and usage figures are not connected yet. The current app is not published on Google Play, so there are no Play Store install counts to display.

To preview locally, open `index.html` or `admin.html` in a browser. To host the site on GitHub Pages, enable Pages in the repository settings and deploy from the `main` branch root.

## Android app

The Android project is in `android/` and uses package ID `com.pocketpulse.app`.

1. Install Android Studio and open the `android/` directory.
2. Add your Firebase Android configuration at `android/app/google-services.json`. Keep it private; this repository ignores it.
3. Review and deploy `android/firestore.rules` to the matching Firebase project.
4. Build in Android Studio, or run `gradlew.bat assembleDebug` from `android/` on Windows.

This repository does not include signing keys, local SDK paths, build outputs, or Firebase configuration credentials. The app has not been released on Google Play.
