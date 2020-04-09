package dev.olog.navigation

import android.view.View
import dev.olog.domain.MediaId
import dev.olog.domain.entity.PlaylistType

interface Navigator {

    fun toFirstAccess()

    fun toDetailFragment(mediaId: MediaId.Category)

    fun toDetailFragment(
        mediaId: MediaId.Category,
        view: View
    )

    fun toRelatedArtists(
        mediaId: MediaId.Category,
        view: View
    )

    fun toRecentlyAdded(
        mediaId: MediaId.Category,
        view: View
    )

    fun toChooseTracksForPlaylistFragment(
        type: PlaylistType,
        view: View
    )

    fun toEditInfoFragment(mediaId: MediaId)

    fun toOfflineLyrics()

    fun toDialog(
        mediaId: MediaId,
        anchor: View,
        container: View?
    )

//    fun toMainPopup(
//        anchor: View,
//        category: MainPopupCategory
//    )

    fun toSetRingtoneDialog(
        mediaId: MediaId.Track,
        title: String,
        artist: String
    )

    fun toCreatePlaylistDialog(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toCreatePlaylistDialogFromPlayingQueue()

    fun toAddToFavoriteDialog(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toPlayLater(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toPlayNext(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toRenameDialog(
        mediaId: MediaId.Category,
        itemTitle: String
    )

    fun toClearPlaylistDialog(
        mediaId: MediaId.Category,
        itemTitle: String
    )

    fun toDeleteDialog(
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toRemoveDuplicatesDialog(
        mediaId: MediaId.Category,
        itemTitle: String
    )
}