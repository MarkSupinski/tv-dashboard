# Battery Dashboard — Google TV app

A Google TV (Android TV / leanback) app that shows a live dashboard of your
ECOWORTHY 314 Ah batteries and their state of charge.

## Features

- **Dashboard** — every battery as an attractive SOC ring card. Navigate with
  the D-pad; press **OK** on a card to open its detail page.
- **Detail page** — full telemetry (voltage, current, power, temperature,
  capacity, health), all per-cell voltages as a bar chart, and a **SOC history
  area chart** with **Hour / Day / Week** range buttons.
- **Auto refresh** — polls the battery server every 60 seconds while running.
- The remote **Back** button (or the *‹ Back* chip) returns to the dashboard.

## Architecture

```
ECO-WORTHY batteries  ⇄  Python battery server (BLE, 60s capture + SQLite)
                              ⇄  this Android app (HTTP/JSON, 60s poll)
```

The Python server lives in `../ecoworthy-battery/battery_server.py` and runs on
a machine near the batteries (Mac, Raspberry Pi, …). The TV app never touches
Bluetooth — it just reads the server's REST API:

| Endpoint | Purpose |
|---|---|
| `GET /api/batteries` | current status of every battery |
| `GET /api/batteries/{address}/history?range=hour\|day\|week` | SOC/voltage/current series |

## Build

Requirements: **Android Studio** (Jellyfish or newer), Android SDK 34.

1. Open this folder (`tv-dashboard/`) in Android Studio.
2. Set the IP/port of your battery server in `gradle.properties`:
   ```properties
   batteryServerHost=192.168.1.100
   batteryServerPort=8234
   ```
3. Build the debug APK: *Build → Build Bundle(s)/APK(s) → Build APK(s)*
   (or `./gradlew assembleDebug` from a terminal).

The project uses the standard Gradle version catalog (`gradle/libs.versions.toml`);
Android Studio will download the Gradle wrapper and dependencies on first sync.

## Install on Google TV

Enable developer options + ADB debugging on the TV, then:

```bash
adb connect <tv-ip>:5555
adb install app/build/outputs/apk/debug/app-debug.apk
```

The app appears as **Battery Dashboard** in the TV launcher.

## Run the server

```bash
cd ../ecoworthy-battery
python battery_server.py --host 0.0.0.0 --port 8234 --interval 60   # live BLE
python battery_server.py --demo --port 8234                          # synthetic demo data
```

`--demo` seeds two batteries with a week of history so you can try the app
without batteries in range. See `../ecoworthy-battery/README.md` for details.
