package dev.olog.data

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import dev.olog.core.coroutines.fireAndForget
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope

internal class DataObserver(
    private val scheduler: CoroutineDispatcher,
    private val onUpdate: () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean) = GlobalScope.fireAndForget(scheduler) {
        onUpdate()
    }
}