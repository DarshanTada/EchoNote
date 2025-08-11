    plugins {
        id("com.android.library")
        id("org.jetbrains.kotlin.android")  // or kotlin if using kotlin, else omit if pure java
    }

    android {
        namespace = "com.example.shared"
        compileSdk = 36

        defaultConfig {
            minSdk = 24
            targetSdk = 36
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        kotlinOptions {
            jvmTarget = "11"
        }
    }

    dependencies {
        implementation(libs.media3.common)
//        implementation(libs.ui.desktop)
        implementation(libs.core)

        // Add other android-specific dependencies here
    }
