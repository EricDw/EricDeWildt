package com.publicmethod.ericdewildt.ui.eric.mvk

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import com.publicmethod.archer.Archer
import com.publicmethod.archer.Archer.FunctionWorkerMessage
import com.publicmethod.archer.Archer.FunctionWorkerMessage.*
import com.publicmethod.archer.Archer.functionWorker
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction.*
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult.*
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext


fun ericProcessor(): Archer.Processor<EricAction, EricResult> =
        object : Archer.Processor<EricAction, EricResult> {

            private var supervisor: Option<SendChannel<EricAction>> = None

            override suspend fun process(
                    action: EricAction,
                    resultChannel: SendChannel<EricResult>
            ) {

                when (action) {
                    is InitializeAction -> processInitializeAction(
                            action,
                            resultChannel
                    )

                    is EmailEricAction -> {
                        processEmailEricAction(
                                action,
                                resultChannel
                        )
                    }

                    is DismissSnackBarAction ->
                        processDismissSnackBarKommand(
                                action,
                                resultChannel
                        )
                }

            }

            private suspend fun processDismissSnackBarKommand(
                    action: DismissSnackBarAction,
                    resultChannel: SendChannel<EricResult>
            ) {
                issueWork(action, resultChannel)
                resultChannel.send(
                        DismissSnackBarResult
                )
            }

            private suspend fun processInitializeAction(
                    action: InitializeAction,
                    resultChannel: SendChannel<EricResult>
            ) {
                resultChannel.send(
                        ShowLoadingResult
                )
                resultChannel.send(
                        InitializeResult(action.getEricScope.ericRepository.getItem())
                )
            }

            private suspend fun processEmailEricAction(
                    action: EmailEricAction,
                    resultChannel: SendChannel<EricResult>
            ) {
                issueWork(action, resultChannel)
                resultChannel.send(EmailEricResult)
            }

            private suspend fun issueWork(
                    action: EricAction,
                    resultChannel: SendChannel<EricResult>
            ) {
                supervisor.fold({
                    val newSupervisor = ericSupervisorActor(
                            ContextProvider().backgroundContext(),
                            resultChannel
                    )
                    supervisor = newSupervisor.some()
                    newSupervisor.send(action)

                }, { supervisor ->
                    supervisor.send(action)
                })
            }

        }

private fun ericSupervisorActor(
        backgroundContext: CoroutineContext,
        resultChannel: SendChannel<EricResult>
) = actor<EricAction>(
        context = backgroundContext
) {

    val emailWorker: SendChannel<FunctionWorkerMessage> by lazy { functionWorker() }

    for (action in channel) when (action) {

        is InitializeAction -> {
        }

        is EmailEricAction -> {
            emailWorker.send(StartWork({
                delay(5L, TimeUnit.SECONDS)
                resultChannel.send(DismissSnackBarResult)
            }))
        }

        is DismissSnackBarAction -> {
            emailWorker.send(
                    StopWork
            )
        }
    }

}