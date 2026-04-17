package com.simplemobiletools.commons.helpers

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

object CommonExecutors {
    private val executors = Executors.newCachedThreadPool()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun execute(callback: () -> Unit) = executors.execute(callback)

    fun executeMainThread(callback: () -> Unit) = mainHandler.post(callback)

    fun postDelayed(delayMillis: Long, callback: () -> Unit) {
        mainHandler.postDelayed(callback, delayMillis)
    }
}


fun executeBackgroundThread(callback: () -> Unit) = CommonExecutors.execute(callback)

fun executeMainThread(callback: () -> Unit) = CommonExecutors.executeMainThread(callback)
