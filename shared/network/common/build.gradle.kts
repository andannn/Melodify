plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.core.network.common"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
        }

        getByName("jvmAndAndroidMain").dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
