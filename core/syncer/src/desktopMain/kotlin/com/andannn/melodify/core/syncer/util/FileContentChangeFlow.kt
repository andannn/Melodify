package com.andannn.melodify.core.syncer.util

import com.andannn.melodify.core.syncer.model.FileChangeEvent
import com.andannn.melodify.core.syncer.model.FileChangeType
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds

private const val TAG = "FileContentChangeFlow"


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
                val events = key.pollEvents()
                    .mapNotNull { event ->
                        val kind = event.kind()
                        val changedFilePath = (event.context() as Path)

                        when (kind) {
                            StandardWatchEventKinds.ENTRY_CREATE -> {
                                FileChangeEvent(
                                    changedFilePath.toUri().toString(),
                                    FileChangeType.CREATE
                                )
                            }

                            StandardWatchEventKinds.ENTRY_DELETE -> {
                                FileChangeEvent(
                                    changedFilePath.toUri().toString(),
                                    FileChangeType.DELETE
                                )
                            }

                            StandardWatchEventKinds.ENTRY_MODIFY -> {
                                FileChangeEvent(
                                    changedFilePath.toUri().toString(),
                                    FileChangeType.MODIFY
                                )
                            }

                            else -> {
                                null
                            }
                        }
                    }
                    .filter { isAudioFile(it.fileUri) }

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