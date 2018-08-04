package com.publicmethod.ericdewildt.ui.eric.mvk

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction.InitializeAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult.InitializeResult
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

fun ericProcessor(
        parentJob: Job,
        backgroundContext: CoroutineContext
): SendChannel<Archer.Message<EricAction>> = actor(parent = parentJob,
        context = backgroundContext,
        capacity = Channel.UNLIMITED) {

    fun sendResult(result: EricResult, returnChannel: SendChannel<Archer.FletchingMessage>) {
        launch(backgroundContext) {
            returnChannel.send(Archer.FletchingMessage.ResultMessage(result))
        }
    }

    fun processInitializeAction(
            action: InitializeAction,
            returnChannel: SendChannel<Archer.FletchingMessage>
    ) {
        sendResult(EricResult.ShowLoadingResult, returnChannel)
        sendResult(InitializeResult(action.getEricScope.ericRepository.getItem()), returnChannel)
    }

    fun processEmailEricAction(returnChannel: SendChannel<Archer.FletchingMessage>) {
        sendResult(EricResult.EmailEricResult, returnChannel)
    }

    for (msg in channel) when (msg.data) {
        is InitializeAction -> processInitializeAction(
                msg.data as InitializeAction,
                msg.returnChannel
        )

        is EricAction.EmailEricAction -> processEmailEricAction(msg.returnChannel)
    }
}




