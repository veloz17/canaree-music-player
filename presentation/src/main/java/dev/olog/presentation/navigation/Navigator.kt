package dev.olog.presentation.navigation

interface Navigator {

    fun toMainActivity()

    fun toDetailActivity(mediaId: String, position: Int)

    fun toRelatedArtists(mediaId: String)

    fun toDialog(mediaId: String, position: Int)

    fun toSetRingtoneDialog(mediaId: String)

}