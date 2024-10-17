plugins {
    id("melodify.kmp.application")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.service)
    alias(libs.plugins.firebase.crashlytics)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":core:datastore"))
            implementation(project(":core:database"))
            implementation(project(":core:network"))
            implementation(project(":core:player"))

            implementation(project(":feature:common"))
            implementation(project(":feature:home"))
            implementation(project(":feature:player"))
            implementation(project(":feature:customtab"))
            implementation(project(":feature:playlist"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)

            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.navigation.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.activity.compose)

            // Firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
        }
    }
}

android {
    namespace = "com.andannn.melodify"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.andannn.melodify"
        versionCode = 16
        versionName = "0.2.0"

        signingConfig = signingConfigs.getByName("debug")
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore/keystore.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
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

            signingConfig = signingConfigs.getByName("release")
        }
    }
}

tasks.register("moveKeyStoreRelease") {
    if (project.gradle.startParameter.taskNames.any { it.contains("Release") }) {
        val tmpFilePath = System.getProperty("user.home") + "/work/_temp/keystore/"
        val allFilesFromDir = File(tmpFilePath).listFiles()
        if (allFilesFromDir != null) {
            val keystoreFile = allFilesFromDir.firstOrNull()

            if (keystoreFile == null || keystoreFile.name != "keystore.jks") {
                throw GradleException("File not found: $tmpFilePath Aborting build.")
            }

            copy {
                from(keystoreFile.absolutePath)
                into("$projectDir/keystore")
            }
        } else {
            throw GradleException("File not found: $tmpFilePath Aborting build.")
        }
    } else {
        println("Debug mode. skip moving keystore file.")
    }
}

tasks.named("preBuild") {
    dependsOn("moveKeyStoreRelease")
}

