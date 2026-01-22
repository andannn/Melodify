

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
    androidLibrary {
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
