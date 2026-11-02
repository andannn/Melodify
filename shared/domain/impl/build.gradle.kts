plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.domain.impl"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:domain:impl-player-android"))
        }

        commonMain.dependencies {
            implementation(project(":shared:player:sleep-timer"))
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:network:service:lrclib"))
            implementation(project(":shared:datastore"))
            implementation(project(":shared:database"))
            implementation(project(":shared:platform"))
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
