plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.serialization)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.core.network.service.lrclib"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:network:common"))
            implementation(libs.ktor.client.resources)
        }
    }
}
