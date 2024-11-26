// build.gradle.kts (app level)
plugins {
    alias(libs.plugins.android.application)  // Apply Android plugin
    alias(libs.plugins.google.gms.google.services)  // Apply Firebase plugin
}

android {
    namespace = "com.example.reminderapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reminderapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    buildFeatures {
        buildConfig = true // Mengaktifkan BuildConfig
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth) // Firebase Authentication
    implementation(libs.firebase.database) // Firebase Realtime Database
    implementation("androidx.cardview:cardview:1.0.0")  // CardView dependency
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.10.0")
    implementation("com.google.firebase:firebase-database:20.3.3") // Realtime Database
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


}

// Apply the Firebase plugin at the end of the file
apply(plugin = "com.google.gms.google-services")
