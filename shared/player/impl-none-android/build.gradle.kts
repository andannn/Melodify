plugins {
    id("kmp.ext")
}

kmpExt {
    withDesktopTarget()
    withIOSTarget()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:player:common"))
        }

        afterEvaluate {
            jvmMain.dependencies {
                implementation(libs.vlcj)
            }
        }

        iosMain.dependencies {
            implementation(project(":shared:player:platform-player-ios"))
            implementation(project(":shared:network:service:siren"))
        }
    }
}
