package com.publicmethod.archer

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.map

class LifecycleListener(private val job: Job) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}

class OnClickLoader<out T>(
        val lifecycle: Lifecycle,
        val view: View,
        private val loadFunction: () -> T
) {
    infix fun then(uiFunction: (T) -> Unit) {
        val job = Job()
        val actor = actor<Unit>(context = UI, parent = job) {
            channel.map(CommonPool) { loadFunction() }
                    .consumeEach { uiFunction(it) }
        }

        lifecycle.addObserver(LifecycleListener(job))

        view.setOnClickListener { actor.offer(Unit) }
    }
}

fun <T> LifecycleOwner.whenClicking(
        view: View,
        loadFunction: () -> T)
        : OnClickLoader<T> = OnClickLoader(
        lifecycle = lifecycle,
        view = view,
        loadFunction = loadFunction
)
