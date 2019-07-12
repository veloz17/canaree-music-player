package dev.olog.service.music

import android.media.AudioManager
import android.view.KeyEvent
import javax.inject.Inject

class EventDispatcher @Inject constructor(
    private val audioManager: AudioManager
) {

    enum class Event {
        PLAY_PAUSE,
        PLAY,
        PAUSE,
        STOP,
        SKIP_NEXT,
        SKIP_PREVIOUS,
        TRACK_ENDED
    }

    fun dispatchEvent(event: Event) {
        val keycode = when (event) {
            Event.PLAY_PAUSE -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            Event.PLAY -> KeyEvent.KEYCODE_MEDIA_PLAY
            Event.PAUSE -> KeyEvent.KEYCODE_MEDIA_PAUSE
            Event.STOP -> KeyEvent.KEYCODE_MEDIA_STOP
            Event.SKIP_NEXT -> KeyEvent.KEYCODE_MEDIA_NEXT
            Event.SKIP_PREVIOUS -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
            Event.TRACK_ENDED -> KeyEvent.KEYCODE_MEDIA_FAST_FORWARD // TODO bad
        }
        audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keycode))
        audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keycode))
    }

}