plugins {
    id("kmp.ext")
}

kmpExt {
    withDesktopTarget()
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
    }
}
