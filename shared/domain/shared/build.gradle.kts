plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

android {
    namespace = "com.andannn.melodify.domain.impl.shared"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            implementation(project(":shared:database"))
            implementation(project(":shared:network"))
        }
    }
}
