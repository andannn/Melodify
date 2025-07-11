plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.material.color.utilities.android)
            implementation(libs.androidx.palette)
        }

        commonMain.dependencies {
            implementation(project(":core:data"))
            api(project(":core:platform"))

            implementation(libs.coil3.compose)
            implementation(libs.reorderable)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = auto
}

android {
    sourceSets["main"].resources {
        srcDirs("src/androidMain/res")
    }

    namespace = "com.andannn.melodify.common"
}
