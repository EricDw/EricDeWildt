package com.publicmethod.ericdewildt.ui.eric.bow.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand.InitializeCommand
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricInterpreterState
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun interpretEricCommand(
        command: EricCommand,
        processor: SendChannel<EricAction>
): State<Option<EricInterpreterState>, Option<EricInterpreterState>> =
        State { optionState ->
            return@State optionState.fold({
                None toT None
            }, { nonOptionState ->
                with(when (command) {
                    is EricCommand.InitializeCommand ->
                        interpretInitializeCommand(
                                command,
                                nonOptionState
                        )

                    is EricCommand.EmailEricCommand ->
                        interpretEmailEricCommand(nonOptionState)

                    is EricCommand.DismissSnackBarCommand ->
                        interpretDismissSnackBarCommand(nonOptionState)
                }) {

                    b.map { action ->
                        launch {
                            processor.send(action)
                        }
                    }
                    Some(a) toT Some(a)
                }
            })
        }

private fun interpretInitializeCommand(
        command: InitializeCommand,
        ericState: EricInterpreterState
): Tuple2<EricInterpreterState, Option<EricAction>> =
        when (ericState.isInitialized) {
            false -> ericState.copy(isInitialized = true) toT
                    Some(EricAction.InitializeAction(command.getEricScope))
            true -> ericState toT None
        }

private fun interpretDismissSnackBarCommand(
        interpreterState: EricInterpreterState
): Tuple2<EricInterpreterState, Option<EricAction>> =
        interpreterState toT Some(EricAction.DismissSnackBarAction)


private fun interpretEmailEricCommand(
        interpreterState: EricInterpreterState
): Tuple2<EricInterpreterState, Option<EricAction>> =
        interpreterState toT Some(EricAction.EmailEricAction)