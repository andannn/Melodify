plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

android {
    namespace = "com.andannn.melodify.core.platform"
}
