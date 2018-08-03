package com.publicmethod.ericdewildt.ui.eric.mvk

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext


fun ericInterpreter(
        parentJob: Job,
        backgroundContext: CoroutineContext,
        fletching: SendChannel<Archer.FletchingMessage>)
        : SendChannel<EricKommand> = actor(
        parent = parentJob,
        context = backgroundContext,
        capacity = Channel.UNLIMITED) {

    var isInitialized = false

    fun sendAction(ericAction: EricAction) {
        launch(backgroundContext) {
            fletching.send(
                    Archer.FletchingMessage.ActionMessage(ericAction))
        }
    }

    fun interpretInitializeCommand(command: EricKommand.InitializeKommand) {
        if (!isInitialized) {
            isInitialized = true
            sendAction(EricAction.InitializeAction(command.getEricScope))
        }
    }

    fun interpretEmailEricCommand(command: EricKommand.EmailEricKommand) {
        sendAction(EricAction.EmailEricAction)
    }

    for (command in channel) {
        when (command) {
            is EricKommand.InitializeKommand -> interpretInitializeCommand(command)
            is EricKommand.EmailEricKommand -> interpretEmailEricCommand(command)
        }
    }
}

