plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "de.msdevs.einschlafhilfe"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.msdevs.einschlafhilfe"
        minSdk = 26
        targetSdk = 34
        versionCode = 52
        versionName = "5.2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.10")
    implementation("com.github.AppIntro:AppIntro:6.3.1")
    implementation("com.mikepenz:aboutlibraries-core:10.9.1")
    implementation("com.mikepenz:aboutlibraries:10.9.1")
    implementation("androidx.activity:activity:1.8.1")
}
