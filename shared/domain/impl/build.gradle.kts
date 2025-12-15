plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

android {
    namespace = "com.andannn.melodify.domain.impl"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:domain:impl-player-android"))
        }
        getByName("desktopMain").dependencies {
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
