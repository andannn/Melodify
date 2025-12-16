import ComposeApp
import MediaPlayer

final class MPMediaScannerImpl : MPMediaScanner {
    func loadAllLocalSongs() -> [AudioData] {
        let mpItems = loadAllMPMediaItems()
        let audioData = mpItems.compactMap { item in
            item.toAudioData()
        }
        
        return audioData
    }
}

private func loadAllMPMediaItems() -> [MPMediaItem] {
    let query = MPMediaQuery.songs()

    let isNotCloudItemPredicate = MPMediaPropertyPredicate(
        value: false,
        forProperty: MPMediaItemPropertyIsCloudItem
    )
    query.addFilterPredicate(isNotCloudItemPredicate)

    return query.items ?? []
}


extension MPMediaItem {
    func toAudioData() -> AudioData? {
        guard let url = self.assetURL else { return nil }

        print("url \(url)")
        let id = Int64(self.persistentID)
        print("id \(id)")

        let durationSeconds = self.playbackDuration
        let durationMsInt32 = Int32(durationSeconds * 1000.0)
        let duration = KotlinInt(value: durationMsInt32)

        let albumPid = Int64(self.albumPersistentID)
        let artistPid = Int64(self.artistPersistentID)

        let albumId = albumPid != 0 ? KotlinLong(value: albumPid) : nil
        let artistId = artistPid != 0 ? KotlinLong(value: artistPid) : nil

        let trackNumber = self.albumTrackNumber > 0
            ? KotlinInt(value: Int32(self.albumTrackNumber))
            : nil
        let numTracks = self.albumTrackCount > 0
            ? KotlinInt(value: Int32(self.albumTrackCount))
            : nil
        let discNumber = self.discNumber > 0
            ? KotlinInt(value: Int32(self.discNumber))
            : nil

        // TODO
        let yearString: String?
        yearString = nil

        let coverUri = MPMediaScannerKt.createCustomArtworkUri(persistentID: id)

        return AudioData(
            id: id,
            path: url.path,
            sourceUri: url.absoluteString,
            title: self.title ?? "Unknown Title",
            duration: duration,
            modifiedDate: nil,
            size: nil,
            mimeType: mimeType(for: url),
            album: self.albumTitle,
            albumId: albumId,
            artist: self.artist,
            artistId: artistId,
            cdTrackNumber: trackNumber,
            discNumber: discNumber,
            numTracks: numTracks,
            bitrate: nil,
            genre: self.genre,
            genreId: nil,
            year: yearString,
            composer: self.composer,
            cover: coverUri
        )
    }
}

private func mimeType(for url: URL?) -> String? {
    guard let ext = url?.pathExtension.lowercased() else { return nil }
    switch ext {
    case "m4a", "mp4": return "audio/mp4"
    case "mp3": return "audio/mpeg"
    case "wav": return "audio/wav"
    case "aiff", "aif": return "audio/aiff"
    default: return nil
    }
}
