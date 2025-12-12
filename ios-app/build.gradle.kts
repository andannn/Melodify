
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true

            export(project(":shared:syncer-api"))
            export(project(":shared:player-api"))
            export(project(":shared:util:orientation"))
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(project(":mobile-ui:common"))
            implementation(project(":mobile-ui:app"))
            api(project(":shared:syncer-api"))
            api(project(":shared:player-api"))
            api(project(":shared:util:orientation"))

            implementation(libs.napier)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
    }
}
