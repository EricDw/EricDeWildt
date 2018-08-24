package com.publicmethod.ericdewildt.ui.eric.bow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Some
import com.publicmethod.archer.Bow
import com.publicmethod.archer.bow
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.interpretEricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.processEricAction
import com.publicmethod.ericdewildt.ui.eric.bow.pipeline.reduceEricResult
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricInterpreterState
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricProcessorState
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlin.coroutines.experimental.CoroutineContext

class EricViewModel(
    backgroundContext: CoroutineContext =
        ContextProvider().backgroundContext(),
    private val parentJob: Job =
        Job(),
    private val bow: Bow<EricAction, EricResult, EricCommand, EricState> =
        bow(
            initialInterpreterState =
            Some(EricInterpreterState()),
            initialProcessorState =
            Some(EricProcessorState(backgroundContext)),
            initialReducerState =
            Some(EricState(backgroundContext)),
            interpret =
            ::interpretEricCommand,
            process =
            ::processEricAction,
            reduce =
            ::reduceEricResult,
            backgroundContext =
            backgroundContext,
            parent =
            parentJob
        )
) : ViewModel(),
    SendChannel<EricCommand> by bow.commandChannel() {

    private val mutableStateLiveData: MutableLiveData<EricState> = MutableLiveData()

    val state: LiveData<EricState>
        get() = mutableStateLiveData

    private val router = produce<Unit>(
        context = backgroundContext,
        parent = parentJob
    ) {
        for (state in bow.stateChannel()) {
            mutableStateLiveData.postValue(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        bow.close()
        parentJob.cancel()
    }
}
