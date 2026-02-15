

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
        namespace = "com.andannn.melodify.core.syncer.scanner.api"
    }
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:scanner:common"))
        }
    }
}
