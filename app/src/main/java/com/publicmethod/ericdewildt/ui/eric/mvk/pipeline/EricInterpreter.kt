package com.publicmethod.ericdewildt.ui.eric.mvk.pipeline

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand.*
import kotlinx.coroutines.experimental.channels.SendChannel


fun ericInterpreter(): Archer.Interpreter<EricKommand, EricAction> =
        object : Archer.Interpreter<EricKommand, EricAction> {
            var isInitialized = false

            override suspend fun interpret(
                    kommand: EricKommand,
                    actionChannel: SendChannel<EricAction>
            ) {
                when (kommand) {
                    is InitializeKommand -> interpretInitializeCommand(
                            kommand,
                            actionChannel
                    )
                    is EmailEricKommand -> interpretEmailEricCommand(actionChannel)
                    is DismissSnackBarKommand -> interpretDismissSnackBarKommand(actionChannel)
                }

            }

            private suspend fun interpretDismissSnackBarKommand(actionChannel: SendChannel<EricAction>) {
                actionChannel.send(EricAction.DismissSnackBarAction)
            }

            private suspend fun interpretInitializeCommand(
                    command: InitializeKommand,
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

