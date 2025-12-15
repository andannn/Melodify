plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
    withDesktop()
}

android {
    namespace = "com.andannn.melodify.shared.compose.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:compose:resource"))
            api(project(":shared:domain:api"))

            api(libs.retainedmodel)
            implementation(libs.coil3.compose)
            implementation(libs.reorderable)
        }
    }
}
