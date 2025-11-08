import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("melodify.kmp.application")
    id("melodify.compose.multiplatform.application")
}

android {
    namespace = "com.andannn.melodify"
}

kotlin {
    sourceSets {
        desktopMain.dependencies {
            implementation(project(":shared:ui"))
            implementation(project(":shared:data"))
            implementation(project(":shared:syncer"))
            implementation(project(":shared:platform"))

            implementation(libs.kotlinx.coroutines.swing)
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
// TODO: Can not launch app by launcher. disable proguard.
            isEnabled = false
            version.set("7.4.0")
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}
