import com.android.build.api.dsl.androidLibrary

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
        namespace = "com.andannn.melodify.domain.impl.shared"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            implementation(project(":shared:database"))
            implementation(project(":shared:network"))
        }
    }
}
