import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Style
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("melodify.kmp.application")
    id("melodify.compose.multiplatform.application")
    alias(libs.plugins.google.service)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.dependency.graph.generator)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            implementation(project(":core:syncer"))
            implementation(project(":core:platform"))

            implementation(project(":ui:common"))
            implementation(project(":ui:components"))

            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.navigation.compose)
            implementation(libs.reorderable)
        }

        androidMain.dependencies {
            implementation(project(":core:player"))

            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.activity.compose)

            // Firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
        }

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.andannn.melodify"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.andannn.melodify"
        versionCode = 26
        versionName = "0.2.10"

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

compose.desktop {
    application {
        mainClass = "com.andannn.melodify.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.andannn.melodify"
            packageVersion = "1.0.0"
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

// command: ./gradlew generateDependencyGraphApp
dependencyGraphGenerator {
    generators.create("app") {
        include =
            { dependency -> dependency.moduleGroup.startsWith("Melodify") }
        children = { true }
        dependencyNode = { node, dependency ->
            if (dependency.moduleGroup.contains("feature")) {
                node.add(
                    Style.FILLED,
                    Color.rgb("#c6f68d")
                )
            } else if (dependency.moduleName == "data" || dependency.moduleName == "syncer") {
                node.add(
                    Style.FILLED,
                    Color.rgb("#ffddb0")
                )
            } else if (dependency.moduleGroup.contains("core")) {
                node.add(
                    Style.FILLED,
                    Color.rgb("#b39afd")
                )
            } else {
                node
            }
        }
    }
}
