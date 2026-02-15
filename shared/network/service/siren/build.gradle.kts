plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.serialization)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withDesktopTarget()
    withIOSTarget()
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
