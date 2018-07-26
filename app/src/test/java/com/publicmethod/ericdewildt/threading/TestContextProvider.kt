package com.publicmethod.ericdewildt.threading

import kotlinx.coroutines.experimental.Unconfined
import kotlin.coroutines.experimental.CoroutineContext

open class TestContextProvider : ContextProvider() {
    override val uiContext : CoroutineContext = Unconfined
    override val backgroundContext : CoroutineContext = Unconfined
}