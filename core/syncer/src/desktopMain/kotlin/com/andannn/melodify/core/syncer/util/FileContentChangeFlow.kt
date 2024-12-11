package com.andannn.melodify.core.syncer.util

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import kotlin.io.path.absolutePathString

private const val TAG = "FileContentChangeFlow"

enum class FileChangeType {
    CREATE,
    DELETE,
    MODIFY
}

/**
 * File change event.
 *
 * @param filePath the file path.
 * @param fileChangeType the file change type.
 */
data class FileChangeEvent(
    val filePath: String,
    val fileChangeType: FileChangeType
)

/**
 * Get the flow of file content change.
 *
 * @param dictionary the directory to watch.
 * @return the flow of file content change.
 */
fun getDirectoryChangeFlow(dictionary: String): Flow<List<FileChangeEvent>> {
    return callbackFlow {
        withContext(Dispatchers.IO) {
            val watchService = try {
                FileSystems.getDefault().newWatchService()
            } catch (e: IOException) {
                Napier.d(tag = TAG) { "failed to open watch service: ${e.message}" }
                throw e
            }

            val path = Paths.get(dictionary)
            path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )
            while (coroutineContext.isActive) {
                val key = watchService.take()
                val events =key.pollEvents()
                    .mapNotNull { event ->
                        val kind = event.kind()
                        val changedFilePath = (event.context() as Path).absolutePathString()

                        when (kind) {
                            StandardWatchEventKinds.ENTRY_CREATE -> {
                                FileChangeEvent(changedFilePath, FileChangeType.CREATE)
                            }

                            StandardWatchEventKinds.ENTRY_DELETE -> {
                                FileChangeEvent(changedFilePath, FileChangeType.DELETE)
                            }

                            StandardWatchEventKinds.ENTRY_MODIFY -> {
                                FileChangeEvent(changedFilePath, FileChangeType.MODIFY)
                            }

                            else -> {
                                null
                            }
                        }
                    }
                    .filter { isAudioFile(it.filePath) }

                if (events.isNotEmpty()) {
                    trySend(events)
                }

                Napier.d(tag = TAG) { "handle event change: $events" }
                val valid = key.reset()
                if (!valid) {
                    Napier.d(tag = TAG) { "watch service stopped." }
                    break
                }
            }

            channel.close()
            watchService.close()
        }
    }
}