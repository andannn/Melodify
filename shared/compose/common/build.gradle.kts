plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
    withDesktopTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.shared.compose.common"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:compose:resource"))
            api(project(":shared:domain:api"))
            api(libs.retainedmodel)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.network.ktor3)
            implementation(libs.reorderable)
        }

        if (project.isConfigIOS()) {
            iosMain.dependencies {
                implementation(project(":shared:util:artwork-ios"))
                implementation(project(":shared:syncer:scanner:platform-ios"))
            }
        }
    }
}
