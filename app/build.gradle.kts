import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Machine-local settings and secrets (Home Assistant host/token) are read from
// the git-ignored local.properties first, falling back to gradle.properties.
val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    FileInputStream(localPropsFile).use { localProps.load(it) }
}
fun haProp(name: String, default: String): String =
    localProps.getProperty(name)?.takeIf { it.isNotBlank() }
        ?: project.findProperty(name)?.toString()?.takeIf { it.isNotBlank() }
        ?: default

android {
    namespace = "com.microserve.batterytv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.microserve.batterytv"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables { useSupportLibrary = true }

        // Point the app at the Home Assistant instance hosting the ECOWORTHY
        // battery integration (override in local.properties or gradle.properties).
        buildConfigField(
            "String",
            "HA_HOST",
            "\"${haProp("haHost", "homeassistant.local")}\"",
        )
        buildConfigField(
            "int",
            "HA_PORT",
            haProp("haPort", "8123"),
        )
        buildConfigField(
            "String",
            "HA_TOKEN",
            "\"${haProp("haToken", "")}\"",
        )
        // Entity-id prefix / friendly-name marker used to discover the
        // integration's battery sensors in Home Assistant.
        buildConfigField(
            "String",
            "HA_ENTITY_PREFIX",
            "\"${haProp("haEntityPrefix", "sensor.eco_worthy_0b_")}\"",
        )
        buildConfigField(
            "String",
            "HA_BATTERY_MARKER",
            "\"${haProp("haBatteryMarker", "ECO-WORTHY")}\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.okhttp)
}
