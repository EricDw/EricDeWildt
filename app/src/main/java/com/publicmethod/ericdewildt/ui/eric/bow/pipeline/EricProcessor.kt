package com.publicmethod.ericdewildt.ui.eric.bow.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.archer.*
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction.*
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult.*
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricProcessorState
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

fun processEricAction(
        action: EricAction,
        reducer: SendChannel<EricResult>
): State<Option<EricProcessorState>, Option<EricProcessorState>> =
        State { optionState ->
            optionState.fold({
                None toT None
            }, { nonOptionState ->
                return@State with(when (action) {
                    is InitializeAction ->
                        processInitializeAction(
                                action,
                                reducer,
                                nonOptionState
                        )

                    is EmailEricAction -> {
                        processEmailEricAction(
                                action,
                                reducer,
                                nonOptionState
                        )
                    }

                    is DismissSnackBarAction ->
                        processDismissSnackBarKommand(
                                action,
                                reducer,
                                nonOptionState
                        )
                }) {
                    b.map { result ->
                        launch {
                            reducer.send(result)
                        }
                    }
                    Some(a) toT Some(a)
                }
            })

        }

fun processInitializeAction(
        action: InitializeAction,
        reducer: SendChannel<EricResult>,
        state: EricProcessorState
): Tuple2<EricProcessorState, Option<EricResult>> {
    launch(state.backgroundContext) {
        reducer.send(
                ShowLoadingResult
        )
    }

    return state toT
            Some(InitializeResult(
                    action.getEricScope.ericRepository.getItem()
            ))

}

fun processEmailEricAction(
        action: EmailEricAction,
        reducer: SendChannel<EricResult>,
        state: EricProcessorState
): Tuple2<EricProcessorState, Option<EricResult>> =
        issueWork(action, reducer, state) toT Some(EmailEricResult)

fun processDismissSnackBarKommand(
        action: EricAction.DismissSnackBarAction,
        reducer: SendChannel<EricResult>,
        state: EricProcessorState
): Tuple2<EricProcessorState, Option<EricResult>> =
        issueWork(action, reducer, state) toT
                Some(DismissSnackBarResult)


fun issueWork(
        action: EricAction,
        resultChannel: SendChannel<EricResult>,
        state: EricProcessorState
): EricProcessorState =
        state.supervisor.fold({
            val newSupervisor = ericSupervisorActor(
                    state.backgroundContext,
                    resultChannel
            )
            val newState = state.copy(supervisor = Some(newSupervisor))
            launch(newState.backgroundContext) {
                newSupervisor.send(action)
            }
            newState
        }, { supervisor ->
            launch {
                supervisor.send(action)
            }
            state
        })


fun ericSupervisorActor(
        backgroundContext: CoroutineContext,
        resultChannel: SendChannel<EricResult>
): SendChannel<EricAction> = actor(
        context = backgroundContext
) {

    val worker: FunctionWorker by lazy { functionWorker(backgroundContext = backgroundContext) }

    val dismissSnackBarKey = "dismissEmail"

    for (action in channel) when (action) {

        is InitializeAction -> {
        }

        is EmailEricAction -> {
            worker.startOrRestartWork(listOf(
                    dismissSnackBarKey to suspend {
                        launch(backgroundContext) {
                            delay(2L, TimeUnit.SECONDS)
                            resultChannel.send(DismissSnackBarResult)
                        }
                    })
            )
        }

        is DismissSnackBarAction -> {
            worker.stopWork(dismissSnackBarKey, null)
        }
    }

}


