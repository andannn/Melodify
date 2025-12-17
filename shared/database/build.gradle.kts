import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kmpExt {
    withAndroid {
        enableHostTest = false
        enableDeviceTest = true
        includeDeviceTestToCommonTest = true
    }
    withDesktop()
    withIOS()
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.ui.database"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:platform"))
            implementation(libs.room.runtime)
            implementation(libs.okio)
            implementation(libs.room.paging)
        }

        iosMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.room.runtime)
        }

        commonTest.dependencies {
            implementation(libs.androidx.room.testing)
            implementation(libs.okio)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}
