# Battery Dashboard — Google TV app

A Google TV (Android TV / leanback) app that shows a live dashboard of your
ECOWORTHY 314 Ah batteries and their state of charge.

## Features

- **Dashboard** — every battery as an attractive SOC ring card. Navigate with
  the D-pad; press **OK** on a card to open its detail page.
- **Detail page** — full telemetry (voltage, current, power, temperature,
  capacity, health), all per-cell voltages as a bar chart, and a **SOC history
  area chart** with **Hour / Day / Week** range buttons.
- **Auto refresh** — polls Home Assistant every 60 seconds while running.
- The remote **Back** button (or the *‹ Back* chip) returns to the dashboard.

## Architecture

```
ECO-WORTHY batteries  ⇄  Home Assistant (HA Bluetooth + ecoworthy_battery HACS
                        integration → per-field sensor entities, recorder history)
                              ⇄  this Android app (HA REST API, HTTP/JSON, 60s poll)
```

The batteries are read by the `ecoworthy_battery` Home Assistant integration
(see `../ha-ecoworthy-battery`, published separately) running on your HA host
(e.g. a Home Assistant Yellow with a Bluetooth dongle). Every battery becomes a
device whose sensors share the entity-id prefix `sensor.eco_worthy_0b_<device>_`
(`_state_of_charge`, `_voltage`, `_current`, `_power`, `_temperature`,
`_design_capacity`, `_state_of_health`, `_problem_code`, `_cell_<n>_voltage`).
The TV app never touches Bluetooth — it just reads Home Assistant's REST API:

| Endpoint | Purpose |
|---|---|
| `GET /api/states` | all entity states; the app discovers battery sensors and groups them per battery |
| `GET /api/history/period/{start}?filter_entity_id=sensor.…_state_of_charge&minimal_response` | recorded SOC history for hour/day/week |

Every request uses `Authorization: Bearer <long-lived-access-token>`.

## Build

Requirements: **Android Studio** (Jellyfish or newer), Android SDK 34.

1. Open this folder (`tv-dashboard/`) in Android Studio.
2. Create a Home Assistant **long-lived access token**:
   HA → *Profile* → *Security* → *Long-lived access tokens* → *Create token*.
3. Set the HA host/port in `gradle.properties` and put your token in the
   git-ignored `tv-dashboard/local.properties` (so it never gets committed):
   ```properties
   # gradle.properties
   haHost=homeassistant.local   # or your HA LAN IP, e.g. 192.168.1.50
   haPort=8123
   ```
   ```properties
   # local.properties (git-ignored — keeps the token out of the repo)
   haToken=eyJhbGciOi…           # the long-lived access token
   ```
   If your HA is reachable via mDNS this default host usually works; otherwise
   use the HA IP (the TV must be on the same network). Discovery additionally
   matches `haBatteryMarker` (default `ECO-WORTHY`) against sensor friendly
   names, and `haEntityPrefix` (default `sensor.eco_worthy_0b_`) against
   entity IDs, so renamed devices keep working.
4. Build the debug APK: *Build → Build Bundle(s)/APK(s) → Build APK(s)*
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

## Home Assistant setup

1. Install the `ecoworthy_battery` integration via HACS (custom repository
   `MarkSupinski/ha-ecoworthy-battery`) and add it in
   *Settings → Devices & Services → Add Integration → ECOWORTHY 0B Battery*.
2. The integration reads the batteries over the HA Bluetooth integration every
   60 s (configurable); the HA recorder stores the history the TV app charts.
3. No standalone server needed any more — the old `battery_server.py` REST
   service from the pre-HA setup is retired.

## Troubleshooting

- *"Home Assistant unreachable"* — wrong `haHost`/`haPort`, TV and HA on
  different networks, or the HA instance is behind a firewall.
- *"Home Assistant rejected the token (401)"* — `haToken` is empty, expired,
  or invalid.
- *"No batteries found"* — the HA integration isn't configured/loaded, the
  batteries are out of BLE range, or `haEntityPrefix`/`haBatteryMarker` don't
  match your entity IDs (check *Developer Tools → States* in HA).

