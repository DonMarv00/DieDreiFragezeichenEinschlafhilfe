plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.mikepenz.aboutlibraries.plugin")
    kotlin("plugin.serialization") version "2.4.10"
}

android {
    namespace = "de.msdevs.einschlafhilfe"
    compileSdk = 37

    defaultConfig {
        applicationId = "de.msdevs.einschlafhilfe"
        minSdk = 30
        targetSdk = 37
        versionCode = 55
        versionName = "6.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true                        // NEU
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("com.google.android.material:material:1.14.0")
    implementation("com.github.bumptech.glide:glide:5.0.9")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.9")
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.2.10")

    implementation("androidx.activity:activity-ktx:1.13.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Compose (nur für den Lizenz-Screen)
    implementation(platform("androidx.compose:compose-bom:2025.09.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.13.0")
    debugImplementation("androidx.compose.ui:ui-tooling")   // für @Preview im Studio

    // AboutLibraries 15.x (Compose-UI)
    implementation("com.mikepenz:aboutlibraries-core:15.2.0")
    implementation("com.mikepenz:aboutlibraries-compose-m3:15.2.0")
}