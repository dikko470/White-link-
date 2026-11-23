# White Link

White Link is an Android Bluetooth Classic peer-to-peer messaging prototype. It supports discovery of nearby Bluetooth devices, pairing/connection, and exchanging text messages without mobile data or Wi-Fi Internet.

## Important
- Both phones need Bluetooth and must be nearby.
- Android may require users to pair devices in system Bluetooth settings first.
- Android 12+ requires runtime Bluetooth permissions.
- This prototype is Bluetooth Classic only. It does not yet provide Wi-Fi Direct, automatic mesh relaying, voice messages, photos, or long-range radio.
- For reliable usage, both users should keep the app open while connected.

## Build
GitHub Actions builds a debug APK. Open Actions, select **Build White Link APK**, and run workflow. The APK is attached as an artifact named `White-Link-debug-APK`.
