package com.andannn.melodify.core.database

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION])
actual annotation class IgnoreAndroidUnitTest actual constructor()

actual typealias IgnoreNativeTest = kotlin.test.Ignore
