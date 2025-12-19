plugins {
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withIOS()
}
