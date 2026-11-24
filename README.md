# WHITE-LINK — Offline Bluetooth Chat (starter build)

This is a clean Android Studio project for a simple, direct Bluetooth text-chat prototype.

## What it does
- Uses Bluetooth Classic RFCOMM sockets for direct phone-to-phone text messages.
- Does not use mobile data, airtime, Wi-Fi internet, a server, or Google Play Services.
- Includes device discovery, paired-device listing, a "wait for connection" server mode, and message sending/receiving.

## Important limitations
- Both phones must have Bluetooth and this app installed.
- Bluetooth range is limited (often around 5–10 metres in ordinary conditions; walls and devices affect it).
- The receiving phone must tap **Bari wasu su haɗu da ni** and keep the app open; the sending phone scans and selects it.
- Android may ask users to pair/accept a connection.
- This first build is text chat only. It does not yet include images, group chat, background delivery, or long-distance relay.
- Bluetooth discovery and connection behavior varies by Android version and manufacturer. Test on two real phones before relying on it.
- This project is a starting implementation, not a claim of universal bug-free operation.

## Build with GitHub Actions
1. Create a new GitHub repository.
2. Upload all files in this folder to the repository root.
3. Open **Actions** and run **Build WHITE-LINK APK** (or push to `main`).
4. When the workflow finishes, open its run and download the `WHITE-LINK-debug-apk` artifact.
5. Install the APK on both phones and test.

## Build locally
Open the folder in Android Studio (JDK 17, Android SDK Platform 35). Run:
`gradle assembleDebug`

APK output:
`app/build/outputs/apk/debug/app-debug.apk`

Package: `com.whitelink.app`
Minimum Android: 6.0 (API 23)
Target Android: 12 (API 32)
