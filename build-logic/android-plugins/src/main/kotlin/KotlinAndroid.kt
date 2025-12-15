import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

/**
 * Configure base Kotlin with Android options
 */
fun CommonExtension<*, *, *, *, *, *>.configureKotlinAndroid() {
    defaultConfig.apply {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
