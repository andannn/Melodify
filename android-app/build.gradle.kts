plugins {
    id("melodify.kmp.application")
    id("melodify.compose.multiplatform.application")
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.service)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify"

    sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.andannn.melodify"
        versionCode = 54
        versionName = "1.2.6"

        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            val shouldSign = project.findProperty("android.releaseSigning") == "true"
            if (shouldSign) {
                signingConfigs {
                    create("release") {
                        storeFile = file(System.getenv("SIGNING_KEYSTORE_PATH"))
                        storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                        keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                        keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
                    }
                }
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
}

dependencies {
    implementation(project(":shared:ui"))
    implementation(project(":shared:data"))
    implementation(project(":shared:player"))
    implementation(project(":shared:syncer"))
    implementation(project(":shared:platform"))

    implementation(libs.coil3.compose)
    implementation(libs.androidx.media3.ui.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.palette)
    implementation(libs.material.color.utilities.android)

    // Firebase
    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}
