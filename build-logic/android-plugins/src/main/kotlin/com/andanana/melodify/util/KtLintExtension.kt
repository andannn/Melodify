package com.andanana.melodify.util

import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension

fun Project.configureKtLint(extension: KtlintExtension) {
    with(extension) {
        version.set("1.7.1")
        verbose.set(true)
        debug.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        additionalEditorconfig.set(
            mapOf(
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            ),
        )
        filter {
            exclude("**/generated/**")
            // Skip generated code.
            // https://github.com/JLLeitschuh/ktlint-gradle/issues/522
            exclude { entry ->
                entry.file.toString().contains("generated")
            }
            include("**/kotlin/**")
        }
    }
}
