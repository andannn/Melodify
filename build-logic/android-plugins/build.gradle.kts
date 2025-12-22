plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.kmp.library.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("KmpBaseConventionPlugin") {
            id = "kmp.ext"
            implementationClass = "KmpBaseConventionPlugin"
        }
    }
}
