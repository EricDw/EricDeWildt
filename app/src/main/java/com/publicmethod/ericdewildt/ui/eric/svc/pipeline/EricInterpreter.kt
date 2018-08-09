package com.publicmethod.ericdewildt.ui.eric.svc.pipeline

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.svc.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.svc.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.svc.algebras.EricCommand.*
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlin.coroutines.experimental.CoroutineContext


fun ericInterpreter(backgroundContext: CoroutineContext = ContextProvider().backgroundContext())
        : Archer.Interpreter<EricCommand, EricAction> =
        object : Archer.Interpreter<EricCommand, EricAction> {
            var isInitialized = false

            override suspend fun interpret(
                    command: EricCommand,
                    actionChannel: SendChannel<EricAction>
            ) {
                when (command) {
                    is InitializeCommand -> interpretInitializeCommand(
                            command,
                            actionChannel
                    )
                    is EmailEricCommand -> interpretEmailEricCommand(actionChannel)
                    is DismissSnackBarCommand -> interpretDismissSnackBarKommand(actionChannel)
                }

            }

            private suspend fun interpretDismissSnackBarKommand(actionChannel: SendChannel<EricAction>) {
                actionChannel.send(EricAction.DismissSnackBarAction)
            }

            private suspend fun interpretInitializeCommand(
                    command: InitializeCommand,
                    actionChannel: SendChannel<EricAction>
            ) {
                if (!isInitialized) {
                    isInitialized = true
                    actionChannel.send(EricAction.InitializeAction(command.getEricScope))
                }
            }

            private suspend fun interpretEmailEricCommand(
                    actionChannel: SendChannel<EricAction>
            ) {
                actionChannel.send(EricAction.EmailEricAction)
            }
        }

