package com.publicmethod.ericdewildt.threading

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

open class ContextProvider {
    open val uiContext : CoroutineContext = UI
    open val backgroundContext : CoroutineContext = CommonPool
}