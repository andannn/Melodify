

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.core.syncer.scanner.impl.local"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:scanner:api"))
        }
    }
}
