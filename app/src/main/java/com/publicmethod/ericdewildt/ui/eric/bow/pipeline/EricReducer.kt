package com.publicmethod.ericdewildt.ui.eric.bow.pipeline

import arrow.core.*
import arrow.data.Reader
import arrow.data.State
import com.publicmethod.archer.*
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult.*
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricState
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

fun reduceEricResult(
    context: CoroutineContext = ContextProvider().backgroundContext()
): Reader<Tuple2<EricResult, SendChannel<EricState>>,
        State<Option<EricState>, Option<EricState>>> =
    Reader { (result, stateChannel) ->
        State<Option<EricState>,
                Option<EricState>> { optionalState ->
            optionalState.fold({
                val newState = Some(EricState())
                newState toT newState
            }, { someState ->
                return@State with(
                    when (result) {
                        is InitializeResult ->
                            reducedInitializeResult(
                                result,
                                someState
                            )

                        is LoadingResult ->
                            reducedShowLoadingResult(
                                someState
                            )

                        is EmailEricResult ->
                            reducedEmailEricResult(
                                someState
                            )

                        is DismissSnackBarResult ->
                            reducedDismissSnackBarResult(
                                someState
                            )
                        is EricResult.IssueWorkResult ->
                            reducedIssueWorkResult(
                                result.ericAction,
                                someState,
                                context,
                                result.resultChannel
                            )
                    }
                ) {
                    launch(context) {
                        stateChannel.send(element = this@with)
                    }

                    Some(t = this) toT Some(t = this)
                }
            })
        }.toId()
    }

fun reducedIssueWorkResult(
    ericAction: EricAction,
    someState: EricState,
    context: CoroutineContext,
    resultChannel: SendChannel<EricResult>
): EricState =
    someState.supervisor.fold({
        val newState = someState.copy(
            supervisor = ericSupervisorActor(
                context,
                resultChannel
            ).some()
        )
        newState.supervisor.map {
            launch(context) {
                it.send(ericAction)
            }
        }
        newState
    }, { supervisor ->
        launch(context) {
            supervisor.send(ericAction)
        }
        someState
    })

fun reducedInitializeResult(
    result: InitializeResult,
    state: EricState
): EricState =
    result.eric.fold(
        { error ->
            state.copy(error = Option.fromNullable(error))
        },
        { eric ->
            state.copy(eric = eric.some())
        })

fun reducedShowLoadingResult(
    state: EricState
): EricState =
    state.copy(isLoading = true)

fun reducedEmailEricResult(
    state: EricState
): EricState =
    state.copy(showSnackBar = true)

fun reducedDismissSnackBarResult(
    state: EricState
): EricState =
    state.copy(showSnackBar = false)

fun ericSupervisorActor(
    backgroundContext: CoroutineContext,
    resultChannel: SendChannel<EricResult>
): SendChannel<EricAction> = actor(
    context = backgroundContext
) {

    val worker: FunctionWorker by lazy { functionWorker(backgroundContext = backgroundContext) }

    val dismissSnackBarKey = "dismissEmail"

    for (action in channel) when (action) {

        is EricAction.EmailEricAction -> {
            worker.startOrRestartWork(
                listOf(
                    dismissSnackBarKey to suspend {
                        launch(backgroundContext) {
                            delay(2L, TimeUnit.SECONDS)
                            resultChannel.send(DismissSnackBarResult)
                        }
                    })
            )
        }

        is EricAction.DismissSnackBarAction -> {
            worker.stopWork(dismissSnackBarKey, None)
        }
    }
}