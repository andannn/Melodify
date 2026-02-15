plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withDesktopTarget()
    withIOSTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.domain.impl"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:domain:impl-player-android"))
        }
        jvmMain.dependencies {
            implementation(project(":shared:domain:impl-player-none-android"))
        }
        if (project.isConfigIOS()) {
            iosMain.dependencies {
                implementation(project(":shared:domain:impl-player-none-android"))
            }
        }
        commonMain.dependencies {
            implementation(project(":shared:player:sleep-timer"))
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:network:service:lrclib"))
            implementation(project(":shared:network:service:siren"))
            implementation(project(":shared:datastore"))
            implementation(project(":shared:database"))
            implementation(project(":shared:platform"))
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
