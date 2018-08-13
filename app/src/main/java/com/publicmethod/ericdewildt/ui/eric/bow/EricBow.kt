package com.publicmethod.ericdewildt.ui.eric.bow

import androidx.lifecycle.ViewModel
import arrow.core.Some
import com.publicmethod.archer.bow
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.interpretEricCommands
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.ericProcessor
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.ericReducer
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricInterpreterState
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricProcessorState
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricState
import kotlinx.coroutines.experimental.Job

class EricBow(contextProvider: ContextProvider = ContextProvider())
    : ViewModel() {

    val bow = bow<
            EricAction,
            EricResult,
            EricCommand,
            EricInterpreterState,
            EricProcessorState,
            EricState>(
            initialInterpreterState = Some(EricInterpreterState()),
            initialProcessorState = Some(EricProcessorState()),
            initialReducerState = Some(EricState()),
            interpret = ::interpretEricCommands,
            process =
            Job(),
            contextProvider::backgroundContext,
            ::ericReducer,
            ::ericProcessor,
            ::interpretEricCommands
    )

}
