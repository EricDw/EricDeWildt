package com.publicmethod.ericdewildt.ui.eric.bow

import androidx.lifecycle.ViewModel
import arrow.core.Some
import com.publicmethod.archer.bow
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.interpretEricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.processEricAction
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.ericReducer
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricInterpreterState
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricProcessorState
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricState
import kotlinx.coroutines.experimental.Job

class EricViewModel(contextProvider: ContextProvider = ContextProvider())
    : ViewModel() {

    private val parentJob = Job()

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
            interpret = ::interpretEricCommand,
            process = ::processEricAction,
            reduce = ::ericReducer,
            backgroundContext = contextProvider::backgroundContext,
            parent = parentJob
    )

}
