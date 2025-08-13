plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.echonote"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mobile"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildFeatures {
        compose = true
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

    implementation(libs.play.services.wearable)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(project(":shared"))

    implementation(libs.constraintlayout)
    implementation(libs.wear)
    implementation(libs.gms.play.services.wearable)
    implementation(libs.gson)
}