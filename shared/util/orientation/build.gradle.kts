plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}
android {
    namespace = "com.andannn.melodify.util.orientation"
}

kmpExt {
    withAndroid()
    withIOS()
}
