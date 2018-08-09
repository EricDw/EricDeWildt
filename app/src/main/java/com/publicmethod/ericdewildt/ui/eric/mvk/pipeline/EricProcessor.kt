package com.publicmethod.ericdewildt.ui.eric.mvk.pipeline

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import com.publicmethod.archer.Archer
import com.publicmethod.archer.Archer.addWork
import com.publicmethod.archer.Archer.functionWorker
import com.publicmethod.archer.Archer.startOrRestartWork
import com.publicmethod.archer.Archer.stopWork
import com.publicmethod.archer.FunctionWorker
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


fun ericProcessor(backgroundContext: CoroutineContext = ContextProvider().backgroundContext()): Archer.Processor<EricAction, EricResult> =
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
                            backgroundContext,
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
): SendChannel<EricAction> = actor(
        context = backgroundContext
) {

    val worker: FunctionWorker by lazy { functionWorker(backgroundContext = backgroundContext) }

    val dismissSnackBarKey = "dismissEmail"

    worker.addWork(dismissSnackBarKey) {
        delay(2L, TimeUnit.SECONDS)
        resultChannel.send(DismissSnackBarResult)
    }

    for (action in channel) when (action) {

        is InitializeAction -> {
        }

        is EmailEricAction -> {
            worker.startOrRestartWork(dismissSnackBarKey)
        }

        is DismissSnackBarAction -> {
            worker.stopWork(dismissSnackBarKey)
        }
    }

}


