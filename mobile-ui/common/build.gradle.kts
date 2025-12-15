plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
}

android {
    namespace = "com.andannn.melodify.ui.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:compose:common"))
            api(project(":shared:compose:usecase"))
            api(project(":shared:compose:popup"))
            api(project(":shared:util:orientation"))
            api(project(":shared:util:immersive"))
        }
    }
}
