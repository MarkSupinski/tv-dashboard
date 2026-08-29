import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Machine-local secrets (e.g. the Home Assistant long-lived access token) are
// read from the git-ignored local.properties so they never land in the repo.
val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    FileInputStream(localPropsFile).use { localProps.load(it) }
}
val haToken: String = localProps.getProperty("haToken")
    ?.takeIf { it.isNotBlank() }
    ?: project.findProperty("haToken")?.toString().orEmpty()

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
        // battery integration (overridable from gradle.properties).
        buildConfigField(
            "String",
            "HA_HOST",
            "\"${project.findProperty("haHost") ?: "homeassistant.local"}\"",
        )
        buildConfigField(
            "int",
            "HA_PORT",
            (project.findProperty("haPort") ?: "8123").toString(),
        )
        buildConfigField(
            "String",
            "HA_TOKEN",
            "\"$haToken\"",
        )
        // Entity-id prefix / friendly-name marker used to discover the
        // integration's battery sensors in Home Assistant.
        buildConfigField(
            "String",
            "HA_ENTITY_PREFIX",
            "\"${project.findProperty("haEntityPrefix") ?: "sensor.eco_worthy_0b_"}\"",
        )
        buildConfigField(
            "String",
            "HA_BATTERY_MARKER",
            "\"${project.findProperty("haBatteryMarker") ?: "ECO-WORTHY"}\"",
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
