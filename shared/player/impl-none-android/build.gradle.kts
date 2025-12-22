plugins {
    id("kmp.ext")
}

kmpExt {
    withDesktop()
    withIOS()
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
        }
    }
}
