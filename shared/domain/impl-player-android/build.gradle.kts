plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
}

android {
    namespace = "com.andannn.melodify.domain.impl.android"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:domain:api"))
            implementation(project(":shared:player:impl-android"))
        }
    }
}
