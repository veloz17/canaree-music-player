package dev.olog.msc.domain.interactor.all

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.*
import dev.olog.msc.domain.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ObserveSongListByParamUseCase @Inject constructor(
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val songDataStore: SongGateway,
        private val albumGateway: AlbumGateway,
        private val artistGateway: ArtistGateway,
        private val genreGateway: GenreGateway,
        private val podcastPlaylistGateway: PodcastPlaylistGateway,
        private val podcastGateway: PodcastGateway,
        private val podcastAlbumGateway: PodcastAlbumGateway,
        private val podcastArtistGateway: PodcastArtistGateway

) : FlowUseCaseWithParam<List<Song>, MediaId>() {

    override fun buildUseCase(mediaId: MediaId): Flow<List<Song>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeTrackListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeTrackListByParam(mediaId.categoryId)
            MediaIdCategory.SONGS -> songDataStore.observeAll()
            MediaIdCategory.ALBUMS -> albumGateway.observeTrackListByParam(mediaId.categoryId)
            MediaIdCategory.ARTISTS -> artistGateway.observeTrackListByParam(mediaId.categoryId)
            MediaIdCategory.GENRES -> genreGateway.observeTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS -> podcastGateway.observeAll()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.observeTrackListByParam(mediaId.categoryId)
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }


}