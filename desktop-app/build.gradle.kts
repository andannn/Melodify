import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    id("kmp.ext")
}

kmpExt {
    withDesktop()
}

kotlin {
    sourceSets {
        jvmMain.dependencies {
            implementation(project(":shared:domain:impl"))
            implementation(project(":shared:syncer:impl"))
            implementation(project(":shared:platform"))

            implementation(project(":shared:compose:common"))
            implementation(project(":shared:compose:popup:dialog"))
            implementation(project(":shared:compose:popup:snack-bar"))
            implementation(project(":shared:compose:components:tab"))
            implementation(project(":shared:compose:components:lyrics"))
            implementation(project(":shared:compose:components:queue"))
            implementation(project(":shared:compose:components:search"))
            implementation(project(":shared:compose:components:library-item"))
            implementation(project(":shared:compose:components:library-detail"))
            implementation(project(":shared:compose:components:play-control"))
            implementation(project(":shared:compose:components:tab-content"))
            implementation(project(":shared:compose:components:tab-management"))

            implementation(libs.jetbrains.compose.desktop)
            implementation(libs.kotlinx.coroutines.swing)
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
