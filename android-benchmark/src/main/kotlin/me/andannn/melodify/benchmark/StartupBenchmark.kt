package me.andannn.melodify.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@RequiresApi(Build.VERSION_CODES.Q)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupBaselineProfile() = startup(CompilationMode.DEFAULT)

    @OptIn(ExperimentalMetricApi::class)
    private fun startup(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = "com.andannn.melodify",
            metrics =
                listOf(
                    StartupTimingMetric(),
                    FrameTimingMetric(),
                    MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
                    PowerMetric(PowerMetric.Type.Power()),
                ),
            iterations = 1,
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
        ) {
            startActivityAndWait()

            device.waitForIdle()
        }
}
