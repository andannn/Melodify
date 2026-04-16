import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.room3)
    alias(libs.plugins.ksp)
}

kmpExt {
    withAndroidTarget {
        enableHostTest = false
        enableDeviceTest = true
        includeDeviceTestToCommonTest = true
    }
    withDesktopTarget()
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    android {
        namespace = "com.andannn.melodify.ui.database"
        androidResources.enable = true
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.sqlite.api)
            implementation(project(":shared:platform"))
            implementation(libs.room3.runtime)
            implementation(libs.room3.paging)
            implementation(libs.okio)
        }

        jvmMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.room3.runtime)
        }

        commonTest.dependencies {
            implementation(libs.room3.testing)
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
    add("kspAndroid", libs.room3.compiler)
    add("kspJvm", libs.room3.compiler)
}
