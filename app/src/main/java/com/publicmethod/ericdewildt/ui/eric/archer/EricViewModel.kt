package com.publicmethod.ericdewildt.ui.eric.archer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.publicmethod.archer.Archer
import com.publicmethod.archer.archer
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.archer.pipeline.interpretEricCommand
import com.publicmethod.ericdewildt.ui.eric.archer.pipeline.processEricAction
import com.publicmethod.ericdewildt.ui.eric.archer.pipeline.reduceEricResult
import com.publicmethod.ericdewildt.ui.eric.archer.states.EricState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlin.coroutines.experimental.CoroutineContext

class EricViewModel(
    backgroundContext: CoroutineContext =
        ContextProvider().backgroundContext(),
    private val parentJob: Job =
        Job(),
    private val archer: Archer<EricCommand, EricState> =
        archer<EricAction, EricResult, EricCommand, EricState>(
            context =
            backgroundContext,
            parentJob =
            parentJob,
            interpret =
            interpretEricCommand(),
            process =
            processEricAction(backgroundContext),
            reduce =
            reduceEricResult(backgroundContext)
        )
) : ViewModel(),
    SendChannel<EricCommand> by archer.commandChannel() {

    private val mutableStateLiveData: MutableLiveData<EricState> = MutableLiveData()

    val state: LiveData<EricState>
        get() = mutableStateLiveData

    private val router = produce<Unit>(
        context = backgroundContext,
        parent = parentJob
    ) {
        for (state in archer.stateChannel()) {
            mutableStateLiveData.postValue(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        archer.close()
        parentJob.cancel()
    }
}
