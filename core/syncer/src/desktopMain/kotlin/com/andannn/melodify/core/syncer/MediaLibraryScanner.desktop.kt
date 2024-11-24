package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.syncer.model.MediaDataModel
import kotlinx.coroutines.flow.first

class MediaLibraryScannerImpl(
    private val mediaLibraryDao: MediaLibraryDao,
) : MediaLibraryScanner {
    override suspend fun scanMediaData(): MediaDataModel {
        val allMediaEntity = mediaLibraryDao.getAllMediaFlow().first()
        val mediaMap = allMediaEntity.associateBy { it.id }

        // 1: Get All media from database
        // 2: Scan all files in library path and generated Key.
        // 3: Loop through all files in library path and create new media data list. rules:
        //      - If id exist in db and last_modify_data is same. map db entity to AudioData (skip extract metadata)
        //      - If id exist in db and last_modify_data is different. extract metadata again from file
        //      - If id not exist in db. extract metadata from file and create new AudioData
        // 4: Group album from created AudioData list
        // 5: Group artist from created AudioData list
        // 6: Group genre from created AudioData list

        TODO("Not yet implemented")
    }
}