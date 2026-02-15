

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
        namespace = "com.andannn.melodify.domain.impl.shared"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            implementation(project(":shared:database"))
            implementation(project(":shared:player:common"))
        }
    }
}
