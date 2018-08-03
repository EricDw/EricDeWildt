package com.publicmethod.ericdewildt.ui.eric.mvk

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
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
        backgroundContext: CoroutineContext,
        fletching: SendChannel<Archer.FletchingMessage>
): SendChannel<EricAction> = actor(parent = parentJob,
        context = backgroundContext,
        capacity = Channel.UNLIMITED) {

    fun sendResult(result: EricResult) {
        launch(backgroundContext) {
            fletching.send(Archer.FletchingMessage.ResultMessage(result))
        }
    }

    fun processInitializeAction(action: EricAction.InitializeAction) {
        sendResult(EricResult.ShowLoadingResult)
        sendResult(InitializeResult(action.getEricScope.ericRepository.getItem()))
    }

    fun processEmailEricAction(action: EricAction.EmailEricAction) {
        sendResult(EricResult.EmailEricResult)
    }

    for (action in channel) {
        when (action) {
            is EricAction.InitializeAction -> processInitializeAction(action)
            is EricAction.EmailEricAction -> processEmailEricAction(action)
        }
    }
}




