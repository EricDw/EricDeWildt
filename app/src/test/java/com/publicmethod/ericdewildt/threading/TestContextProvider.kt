package com.publicmethod.ericdewildt.threading

import kotlinx.coroutines.experimental.Unconfined
import kotlin.coroutines.experimental.CoroutineContext

open class TestContextProvider : ContextProvider() {
    override fun uiContext(): CoroutineContext = Unconfined
    override fun backgroundContext(): CoroutineContext = Unconfined
}