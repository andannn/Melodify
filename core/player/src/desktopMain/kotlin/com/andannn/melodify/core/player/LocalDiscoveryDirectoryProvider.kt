package com.andannn.melodify.core.player

import io.github.aakira.napier.Napier
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryProviderPriority
import java.io.File

class LocalDiscoveryDirectoryProvider : DiscoveryDirectoryProvider {
    override fun priority(): Int = DiscoveryProviderPriority.USER_DIR

    override fun directories(): Array<String> {
        val path = System.getProperty("compose.application.resources.dir") ?: return emptyArray()
        val libs = File(path).resolve("lib")
        if (!libs.exists()) return emptyArray()
        Napier.d { "libvlc loaded by LocalDiscoveryDirectoryProvider." }
        return arrayOf(libs.absolutePath)
    }

    override fun supported(): Boolean = true
}
