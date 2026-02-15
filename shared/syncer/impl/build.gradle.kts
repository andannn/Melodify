

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
        namespace = "com.andannn.melodify.core.syncer.impl"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:api"))
            implementation(project(":shared:syncer:scanner:impl-local"))
            implementation(project(":shared:syncer:scanner:impl-monster-siren"))
            implementation(project(":shared:database"))
            implementation(project(":shared:datastore"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.work.runtime.ktx)
        }
    }
}
