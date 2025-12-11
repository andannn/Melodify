import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain = getByName("desktopMain")
        desktopMain.dependencies {
            implementation(project(":shared:data"))
            implementation(project(":shared:syncer"))
            implementation(project(":shared:platform"))

            implementation(project(":shared:compose:common"))
            implementation(project(":shared:compose:popup"))
            implementation(project(":shared:compose:components:tab"))
            implementation(project(":shared:compose:components:lyrics"))
            implementation(project(":shared:compose:components:queue"))
            implementation(project(":shared:compose:components:search"))
            implementation(project(":shared:compose:components:library-item"))
            implementation(project(":shared:compose:components:library-detail"))
            implementation(project(":shared:compose:components:play-control"))
            implementation(project(":shared:compose:components:tab-content"))
            implementation(project(":shared:compose:components:tab-management"))

            implementation(compose.desktop.currentOs)

            implementation(libs.napier)
            implementation(libs.jetbrains.compose.desktop)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.jetbrains.compose.resources)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.material.icons.extended)
            implementation(libs.lifecycle.runtime.compose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }

        val desktopTest = getByName("desktopTest")
        desktopTest.dependencies {
            implementation(compose.desktop.currentOs)
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
