plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    id("kmp.ext")
}

kmpExt {
    withIOS()
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true

            export(project(":shared:syncer:model"))
            export(project(":shared:syncer:platform-ios"))
            export(project(":shared:player:platform-player-ios"))
            export(project(":shared:util:orientation"))
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(project(":shared:domain:impl"))
            implementation(project(":mobile-ui:common"))
            implementation(project(":mobile-ui:app"))
            api(project(":shared:syncer:model"))
            api(project(":shared:syncer:platform-ios"))
            api(project(":shared:player:platform-player-ios"))
            api(project(":shared:util:orientation"))
        }
    }
}
