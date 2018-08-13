package com.publicmethod.ericdewildt.ui.eric.bow.states

import arrow.core.None
import arrow.core.Option
import com.publicmethod.archer.StateData
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import kotlinx.coroutines.experimental.channels.SendChannel

data class EricProcessorState(
        val supervisor: Option<SendChannel<EricAction>> = None
) : StateData