plugins {
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withIOS()
}
