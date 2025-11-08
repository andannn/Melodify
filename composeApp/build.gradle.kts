import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("melodify.kmp.application")
    id("melodify.compose.multiplatform.application")
    alias(libs.plugins.google.service)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        val mobileUiCommon by creating {
            dependsOn(commonMain.get())
        }

        androidMain {
            dependsOn(mobileUiCommon)
        }

        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":core:syncer"))
            implementation(project(":core:platform"))

            implementation(libs.coil3.compose)
            implementation(libs.reorderable)
            implementation(libs.androidx.paging.compose)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(project(":core:player"))

            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.activity.compose)

            implementation(libs.androidx.palette)
            implementation(libs.material.color.utilities.android)
            implementation(libs.androidx.glance.material3)

            // Firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
        }

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }

        androidUnitTest.dependencies {
            implementation(libs.paparazzi)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = auto
}

android {
    namespace = "com.andannn.melodify"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.andannn.melodify"
        versionCode = 48
        versionName = "1.2.0"

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
                        storePassword = System.getenv("KEYSTORE_PASSWORD")
                        keyAlias = System.getenv("KEY_ALIAS")
                        keyPassword = System.getenv("KEY_PASSWORD")
                    }
                }
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.andannn.melodify.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "Melodify"
            packageVersion = "1.1.0"

            modules(
                "jdk.unsupported",
            )
            appResourcesRootDir.set(file("appResources"))
        }

        buildTypes.release.proguard {
            version.set("7.4.0")
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}
