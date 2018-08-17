package com.publicmethod.ericdewildt.threading

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.HandlerContext
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

open class ContextProvider {
    open fun backgroundContext(): CoroutineContext = CommonPool
}