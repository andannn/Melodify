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
    namespace = "com.andannn.melodify.core.network"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
