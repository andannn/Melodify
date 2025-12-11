plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.jetbrainsCompose.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.licensee.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatformLibrary") {
            id = "melodify.kmp.library"
            implementationClass = "KMPLibraryConventionPlugin"
        }
        register("composeMultiplatformLibraryConventionPlugin") {
            id = "melodify.compose.multiplatform.library"
            implementationClass = "ComposeMultiplatformLibraryConventionPlugin"
        }
    }
}
