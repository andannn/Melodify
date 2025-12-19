import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
    withDesktop()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.shared.compose.usecase"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            api(project(":shared:compose:common"))
            api(project(":shared:compose:popup"))
        }
    }
}
