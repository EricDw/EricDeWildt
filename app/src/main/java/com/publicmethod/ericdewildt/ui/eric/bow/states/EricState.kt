package com.publicmethod.ericdewildt.ui.eric.bow.states

import arrow.core.None
import arrow.core.Option
import com.publicmethod.archer.FunctionWorker
import com.publicmethod.archer.StateData
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.algebras.EricError
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlin.coroutines.experimental.CoroutineContext

data class EricState(
    val eric: Option<Eric> = None,
    val error: Option<EricError> = None,
    val navBarEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val showSnackBar: Boolean = false,
    val supervisor: Option<SendChannel<EricAction>> = None
) : StateData

