

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.domain.impl.android"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:domain:api"))
            implementation(project(":shared:player:impl-android"))
        }
    }
}
