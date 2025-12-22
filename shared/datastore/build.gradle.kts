

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
        namespace = "com.andannn.melodify.core.datastore"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:platform"))

            implementation(libs.datastore)
            implementation(libs.okio)
            implementation(libs.datastore.preferences)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.okio)
        }
    }
}
