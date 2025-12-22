

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
        namespace = "com.andannn.melodify.ui.core.player.common"
    }
}
