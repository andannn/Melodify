plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.jetbrainsCompose.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatformLibrary") {
            id = "melodify.kmp.library"
            implementationClass = "KMPLibraryConventionPlugin"
        }
        register("kotlinMultiplatformApplication") {
            id = "melodify.kmp.application"
            implementationClass = "KMPApplicationConventionPlugin"
        }
        register("composeMultiplatformLibraryConventionPlugin") {
            id = "melodify.compose.multiplatform.library"
            implementationClass = "ComposeMultiplatformLibraryConventionPlugin"
        }
        register("composeMultiplatformApplicationConventionPlugin") {
            id = "melodify.compose.multiplatform.application"
            implementationClass = "ComposeMultiplatformApplicationConventionPlugin"
        }
    }
}