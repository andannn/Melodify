package com.andannn.melodify.core.syncer.util

import com.andannn.melodify.core.syncer.model.FileChangeEvent
import com.andannn.melodify.core.syncer.model.FileChangeType
import com.andannn.melodify.core.syncer.model.RefreshType
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.nio.file.WatchService
import kotlin.io.path.isDirectory

private const val TAG = "FileContentChangeFlow"


/**
 * Get the flow of file content change.
 *
 * @param dictionaries the directory to watch.
 * @return the flow of file content change.
 */
fun getDirectoryChangeFlow(dictionaries: List<Path>): Flow<RefreshType> {
    Napier.d(tag = TAG) { "getDirectoryChangeFlow: $dictionaries" }
    return callbackFlow {
        withContext(Dispatchers.IO) {
            val watchService = try {
                FileSystems.getDefault().newWatchService()
            } catch (e: IOException) {
                Napier.d(tag = TAG) { "failed to open watch service: ${e.message}" }
                throw e
            }

            val watchKeyMap = mutableMapOf<WatchKey, Path>()

            dictionaries
                .fold(emptyList<Path>()) { acc, dictionary ->
                    acc + Files.walk(dictionary)
                        .filter {
                            Files.isDirectory(it)
                        }
                        .toList()
                }.forEach { path ->
                    Napier.d(tag = TAG) { "register path: $path" }
                    path.registerAllChange(watchService, watchKeyMap)
                }
            while (coroutineContext.isActive) {
                val key = watchService.take()
                val valid = key.reset()
                if (!valid) {
                    Napier.d(tag = TAG) { "watch service stopped. ${watchKeyMap[key]}" }
                    watchKeyMap.remove(key)
                    key.cancel()
                    trySend(RefreshType.All)
                    continue
                }

                val events = key.pollEvents()
                    .mapNotNull { event ->
                        val kind = event.kind()
                        val changedFilePath = watchKeyMap[key]!!.resolve(event.context() as Path)
                        Napier.d(tag = TAG) { "changedFilePath $changedFilePath, kind $kind" }
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
                    .toSet()

                val dictionaryEvents = events
                    .filter { Paths.get(URI.create(it.fileUri)).isDirectory() }

                // Handle folder modify or create.
                dictionaryEvents
                    .filter { it.fileChangeType == FileChangeType.MODIFY || it.fileChangeType == FileChangeType.CREATE }
                    .forEach {
                        // Register new created folder
                        Paths.get(URI.create(it.fileUri)).registerAllChange(watchService, watchKeyMap)
                    }

                if (dictionaryEvents.isNotEmpty()) {
                    // Rescan all media.
                    trySend(RefreshType.All)

                    continue
                }

                // Audio file change
                events
                    .filter { isAudioFile(it.fileUri) }
                    .let { audioFileChangeEvents ->
                        if (audioFileChangeEvents.isNotEmpty()) {
                            Napier.d(tag = TAG) { "trySend event change: $audioFileChangeEvents" }
                            trySend(RefreshType.ByUri(audioFileChangeEvents))
                        }
                    }
            }

            channel.close()
            watchService.close()
        }
    }
}

private fun Path.registerAllChange(
    watchService: WatchService,
    watchKeyMap: MutableMap<WatchKey, Path>
) {
    val key = register(
        watchService,
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_DELETE,
        StandardWatchEventKinds.ENTRY_MODIFY
    )
    watchKeyMap[key] = this
}