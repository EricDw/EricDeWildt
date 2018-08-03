package com.publicmethod.ericdewildt.ui.eric.mvk

import arrow.core.Option
import arrow.core.some
import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.Archer
import com.publicmethod.archer.Archer.reducerActor
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlin.coroutines.experimental.CoroutineContext

fun ericReducer(
        parentJob: Job,
        backgroundContext: CoroutineContext,
        fletching: SendChannel<Archer.FletchingMessage>,
        initialModel: EricModel
): SendChannel<EricResult> = reducerActor(
        parentJob,
        backgroundContext,
        fletching,
        initialModel,
        ::ericReduction)

fun ericReduction(result: EricResult): State<EricModel, EricState> {

    fun reduceShowLoadingResult()
            : State<EricModel, EricState> =
            State { oldModel ->

                val newModel: EricModel = oldModel.copy(isLoading = true)
                val state: EricState = EricState.ShowLoadingState(newModel)

                newModel toT state

            }

    fun reduceInitializeResult(result: EricResult.InitializeResult)
            : State<EricModel, EricState> =
            State { oldModel ->
                lateinit var newModel: EricModel
                lateinit var state: EricState

                result.eric.fold(
                        { error ->
                            newModel = oldModel.copy(error =
                            Option.fromNullable(error))
                            state = EricState.ShowErrorState(newModel)
                        },
                        { eric ->
                            newModel = oldModel.copy(eric = eric.some())
                            state = EricState.ShowEricState(newModel)
                        })

                newModel toT state
            }

    fun reduceEmailEricResult(result: EricResult.EmailEricResult): State<EricModel, EricState> =
            State { oldModel ->
                oldModel toT EricState.ShowEmailEricState
            }

    return when (result) {
        is EricResult.InitializeResult -> reduceInitializeResult(result)
        is EricResult.ShowLoadingResult -> reduceShowLoadingResult()
        is EricResult.EmailEricResult -> reduceEmailEricResult(result)
    }
}
