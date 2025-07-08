package com.andanana.melodify.util

import app.cash.licensee.LicenseeExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File

fun Project.configureLicense() {
    with(pluginManager) {
        apply("app.cash.licensee")
    }
    licensee {
        allow("Apache-2.0")
        allow("MIT")
        allow("BSD-3-Clause")
        allowUrl("https://opensource.org/license/MIT")
        allowUrl("https://developer.android.com/studio/terms.html")
    }

    tasks.register(
        "updateLicense",
    ) {
        group = "license"
        description = "Copy license files to target locations."
        doFirst {
            LicenseFile.values().forEach { item ->
                if (item.from.exists()) {
                    val parent = rootProject.file(item.to).parentFile
                    if (!parent.exists()) parent.mkdirs()
                    item.from.copyTo(item.to, overwrite = true)
                }
            }
        }
    }
    tasks.named("licensee") {
        dependsOn("updateLicense")
    }
}

private fun Project.licensee(action: LicenseeExtension.() -> Unit) = extensions.configure<LicenseeExtension>(action)

enum class LicenseFile(
    val from: File,
    val to: File,
) {
    Android(
        from = File("composeApp/build/reports/licensee/androidRelease/artifacts.json"),
        to = File("ui/common/src/androidMain/composeResources/files/license/artifacts.json"),
    ),
    IOSArm64(
        from = File("composeApp/build/reports/licensee/iosArm64/artifacts.json"),
        to = File("ui/common/src/iosArm64Main/composeResources/files/license/artifacts.json"),
    ),
    IOSSimulatorArm64(
        from = File("composeApp/build/reports/licensee/iosSimulatorArm64/artifacts.json"),
        to = File("ui/common/src/iosSimulatorArm64Main/composeResources/files/license/artifacts.json"),
    ),
}
