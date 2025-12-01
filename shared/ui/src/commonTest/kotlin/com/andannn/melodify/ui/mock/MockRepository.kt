/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.mock

import androidx.paging.PagingData
import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.model.PresetDisplaySetting
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.TabKind
import com.andannn.melodify.core.data.model.UserSetting
import com.andannn.melodify.core.data.model.VideoItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

abstract class MockRepository : Repository {
    override fun getLyricByMediaIdFlow(
        mediaId: String,
        trackName: String,
        artistName: String,
        albumName: String?,
        duration: Long?,
    ): Flow<LyricRepository.State> {
        TODO("Not yet implemented")
    }

    override fun getAllMediaItemsPagingFlow(
        whereGroup: List<GroupKey>,
        sort: List<SortOption.AudioOption>,
    ): Flow<PagingData<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAllVideoItemsPagingFlow(
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<VideoItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getVideoBucketItemsPagingFlow(
        bucketId: String,
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<VideoItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getVideoBucketItemsFlow(
        bucketId: String,
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<VideoItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAllMediaItemsFlow(
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAllVideoItemsFlow(
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<VideoItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistFlow(): Flow<List<ArtistItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAllGenreFlow(): Flow<List<GenreItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudiosOfAlbumFlow(
        albumId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudiosPagingFlowOfAlbum(
        albumId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudiosOfArtistFlow(
        artistId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudiosPagingFlowOfArtist(
        artistId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudiosOfGenreFlow(
        genreId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudiosPagingFlowOfGenre(
        genreId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?> {
        TODO("Not yet implemented")
    }

    override fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?> {
        TODO("Not yet implemented")
    }

    override fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel? {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistByArtistId(artistId: String): ArtistItemModel? {
        TODO("Not yet implemented")
    }

    override suspend fun getGenreByGenreId(genreId: String): GenreItemModel? {
        TODO("Not yet implemented")
    }

    override suspend fun searchContent(keyword: String): List<MediaItemModel> {
        TODO("Not yet implemented")
    }

    override suspend fun markMediaAsDeleted(mediaIds: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun markVideoAsDeleted(mediaIds: List<String>) {
        TODO("Not yet implemented")
    }

    override fun playMediaList(
        mediaList: List<MediaItemModel>,
        index: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun seekToNext() {
        TODO("Not yet implemented")
    }

    override fun seekToPrevious() {
        TODO("Not yet implemented")
    }

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun seekToTime(time: Long) {
        TODO("Not yet implemented")
    }

    override fun setPlayMode(mode: PlayMode) {
        TODO("Not yet implemented")
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun addMediaItems(
        index: Int,
        mediaItems: List<MediaItemModel>,
    ) {
        TODO("Not yet implemented")
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun removeMediaItem(index: Int) {
        TODO("Not yet implemented")
    }

    override fun getCurrentPositionMs(): Long {
        TODO("Not yet implemented")
    }

    override fun observeCurrentPositionMs(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun getPlayingIndexInQueue(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayListQueue(): List<MediaItemModel> {
        TODO("Not yet implemented")
    }

    override fun getPlayingMediaStateFlow(): Flow<MediaItemModel?> {
        TODO("Not yet implemented")
    }

    override fun getPlayListQueueStateFlow(): Flow<List<MediaItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getCurrentPlayMode(): PlayMode {
        TODO("Not yet implemented")
    }

    override fun observeIsShuffle(): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun observePlayMode(): Flow<PlayMode> {
        TODO("Not yet implemented")
    }

    override fun observeIsPlaying(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun observeProgressFactor(): Flow<Float> {
        TODO("Not yet implemented")
    }

    override fun getAllPlayListFlow(isAudio: Boolean): Flow<List<PlayListItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAllPlayListFlow(): Flow<List<PlayListItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudiosOfPlayListFlow(
        playListId: Long,
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey>,
    ): Flow<List<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getVideosOfPlayListFlow(
        playListId: Long,
        sort: List<SortOption.VideoOption>,
        wheres: List<GroupKey>,
    ): Flow<List<VideoItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getAudioPagingFlowOfPlayList(
        playListId: Long,
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getVideoPagingFlowOfPlayList(
        playListId: Long,
        sort: List<SortOption.VideoOption>,
        wheres: List<GroupKey>,
    ): Flow<PagingData<VideoItemModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayListById(playListId: Long): PlayListItemModel? {
        TODO("Not yet implemented")
    }

    override fun getPlayListFlowById(playListId: Long): Flow<PlayListItemModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun addItemsToPlayList(
        playListId: Long,
        items: List<MediaItemModel>,
    ): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        items: List<MediaItemModel>,
    ): List<String> {
        TODO("Not yet implemented")
    }

    override fun isMediaInFavoritePlayListFlow(
        mediaStoreId: String,
        isAudio: Boolean,
    ): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFavoriteMedia(audio: MediaItemModel) {
        TODO("Not yet implemented")
    }

    override suspend fun removeMusicFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun createNewPlayList(
        name: String,
        isAudio: Boolean,
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlayList(playListId: Long) {
        TODO("Not yet implemented")
    }

    override fun isCounting(): Boolean {
        TODO("Not yet implemented")
    }

    override fun observeIsCounting(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun observeRemainTime(): Flow<Duration> {
        TODO("Not yet implemented")
    }

    override fun startSleepTimer(duration: Duration) {
        TODO("Not yet implemented")
    }

    override fun cancelSleepTimer() {
        TODO("Not yet implemented")
    }

    override val userSettingFlow: Flow<UserSetting>
        get() = TODO("Not yet implemented")
    override val currentCustomTabsFlow: Flow<List<CustomTab>>
        get() = TODO("Not yet implemented")

    override suspend fun addNewCustomTab(
        externalId: String,
        tabName: String,
        tabKind: TabKind,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun isTabExist(
        externalId: String,
        tabName: String,
        tabKind: TabKind,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCustomTab(tab: CustomTab) {
        TODO("Not yet implemented")
    }

    override suspend fun addLibraryPath(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLibraryPath(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addSearchHistory(searchHistory: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSearchHistory(limit: Int): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getLastSuccessfulSyncTime(): Long? {
        TODO("Not yet implemented")
    }

    override suspend fun saveDefaultSortRule(
        isAudio: Boolean,
        preset: PresetDisplaySetting,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun saveSortRuleForTab(
        tab: CustomTab,
        displaySetting: DisplaySetting,
    ) {
        TODO("Not yet implemented")
    }

    override fun getCurrentSortRule(tab: CustomTab): Flow<DisplaySetting> {
        TODO("Not yet implemented")
    }

    override fun getDefaultPresetSortRule(isAudio: Boolean): Flow<DisplaySetting> {
        TODO("Not yet implemented")
    }

    override suspend fun getTabCustomSortRule(tab: CustomTab): DisplaySetting? {
        TODO("Not yet implemented")
    }

    override suspend fun swapTabOrder(
        from: CustomTab,
        to: CustomTab,
    ) {
        TODO("Not yet implemented")
    }
}
