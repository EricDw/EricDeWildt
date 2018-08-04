package com.publicmethod.ericdewildt.ui.eric.mvk

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand.InitializeKommand
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext


fun ericInterpreter(
        parentJob: Job,
        backgroundContext: CoroutineContext)
        : SendChannel<Archer.Message<EricKommand>> = actor(
        parent = parentJob,
        context = backgroundContext,
        capacity = Channel.UNLIMITED) {

    var isInitialized = false

    fun sendAction(ericAction: EricAction, returnChannel: SendChannel<Archer.FletchingMessage>) {
        launch(backgroundContext) {
            returnChannel.send(
                    Archer.FletchingMessage.ActionMessage(ericAction))
        }
    }

    fun interpretInitializeCommand(
            command: InitializeKommand,
            returnChannel: SendChannel<Archer.FletchingMessage>
    ) {
        if (!isInitialized) {
            isInitialized = true
            sendAction(EricAction.InitializeAction(command.getEricScope), returnChannel)
        }
    }

    fun interpretEmailEricCommand(
            returnChannel: SendChannel<Archer.FletchingMessage>
    ) {
        sendAction(EricAction.EmailEricAction, returnChannel)
    }

    for (msg in channel) {
        when (msg.data) {
            is InitializeKommand -> interpretInitializeCommand(
                    msg.data as InitializeKommand,
                    msg.returnChannel
            )
            is EricKommand.EmailEricKommand -> interpretEmailEricCommand(msg.returnChannel)
        }
    }
}
