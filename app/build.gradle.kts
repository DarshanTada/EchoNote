plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.echonote"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.echonote"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(project(":shared"))
    implementation(libs.constraintlayout)
    implementation(libs.google.play.services.wearable)
    implementation(libs.localbroadcastmanager)
}