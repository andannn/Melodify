plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.serialization)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withDesktopTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.core.network.service.siren"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:network:common"))
        }
    }
}
