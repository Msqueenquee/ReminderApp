    // build.gradle.kts (project level)
    plugins {
        alias(libs.plugins.android.application) apply false
        alias(libs.plugins.google.gms.google.services) apply false
    }

    buildscript {
        dependencies {
            // Firebase plugin untuk google-services
            classpath("com.google.gms:google-services:4.3.15")  // Firebase plugin
            classpath("com.android.tools.build:gradle:8.0.0")  // Android Gradle plugin
        }
    }

    task("clean", type = Delete::class) {
        delete(rootProject.buildDir)
    }
