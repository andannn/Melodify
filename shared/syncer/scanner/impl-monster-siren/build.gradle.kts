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
        namespace = "com.andannn.melodify.core.syncer.scanner.impl.siren"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:scanner:api"))
            implementation(project(":shared:network:service:siren"))
        }
    }
}
