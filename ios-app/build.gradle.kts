plugins {
    alias(libs.plugins.compose.compiler)
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

            export(project(":shared:syncer:scanner:common"))
            export(project(":shared:syncer:scanner:platform-ios"))
            export(project(":shared:player:platform-player-ios"))
            export(project(":shared:util:orientation"))
            export(project(":shared:util:immersive"))
            export(project(":shared:util:artwork-ios"))
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(project(":shared:syncer:impl"))
            implementation(project(":shared:domain:impl"))
            implementation(project(":mobile-ui:common"))
            implementation(project(":mobile-ui:app"))
            implementation(project(":shared:util:brightness"))
            implementation(project(":shared:util:volume"))
            api(project(":shared:syncer:scanner:common"))
            api(project(":shared:syncer:scanner:platform-ios"))
            api(project(":shared:player:platform-player-ios"))
            api(project(":shared:util:orientation"))
            api(project(":shared:util:immersive"))
            api(project(":shared:util:artwork-ios"))
        }
    }
}
