plugins {
    id("melodify.kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.domain.impl"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:domain:impl-player-android"))
        }
        desktopMain.dependencies {
            implementation(project(":shared:domain:impl-player-none-android"))
        }
        iosMain.dependencies {
            implementation(project(":shared:domain:impl-player-none-android"))
        }
        commonMain.dependencies {
            api(project(":shared:domain:api"))

            implementation(project(":shared:player:sleep-timer"))
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:datastore"))
            implementation(project(":shared:network"))
            implementation(project(":shared:database"))
            implementation(project(":shared:platform"))
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
