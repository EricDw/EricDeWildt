package com.publicmethod.ericdewildt.threading

import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlin.coroutines.experimental.CoroutineContext

open class TestContextProvider : ContextProvider() {
    override fun backgroundContext(): CoroutineContext = Unconfined
}