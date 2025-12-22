
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.Family

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
        androidResources.enable = true
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

        jvmMain.dependencies {
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

// Copy Db schemas to apple os Main bundle folder
tasks.withType<KotlinNativeLink>().configureEach {
    val konanTarget = binary.target.konanTarget

    val isAppleTarget =
        konanTarget.family in
            listOf(
                Family.IOS,
                Family.TVOS,
                Family.WATCHOS,
            )

    if (isAppleTarget) {
        val inputSchemaDir = layout.projectDirectory.dir("schemas")
        val outputSchemaDir = destinationDirectory.dir("schemas")
        doLast {
            val srcFile = inputSchemaDir.asFile
            val destFile = outputSchemaDir.get().asFile

            if (srcFile.exists()) {
                srcFile.copyRecursively(target = destFile, overwrite = true)

                println("[CopySchemas] Copied schemas to: ${outputSchemaDir.get().asFile.absolutePath}")
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}
