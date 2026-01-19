

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
        namespace = "com.andannn.melodify.core.syncer.scanner.api"
    }
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:scanner:common"))
        }
    }
}
