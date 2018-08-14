package com.publicmethod.ericdewildt.ui.eric.bow.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.archer.OptionalSendChannel
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand.InitializeCommand
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricInterpreterState

fun interpretEricCommand(
        command: EricCommand,
        processor: OptionalSendChannel<EricAction>
): State<Option<EricInterpreterState>, Option<EricAction>> =
        State { state ->
            when (command) {
                is EricCommand.InitializeCommand ->
                    interpretInitializeCommand(
                            command,
                            state
                    )

                is EricCommand.EmailEricCommand ->
                    interpretEmailEricCommand(state)

                is EricCommand.DismissSnackBarCommand ->
                    interpretDismissSnackBarCommand(state)
            }

        }


private fun interpretInitializeCommand(
        command: InitializeCommand,
        ericState: Option<EricInterpreterState>
): Tuple2<Option<EricInterpreterState>, Option<EricAction>> =
        ericState.fold({
            None toT None
        }, { state ->
            when (state.isInitialized) {
                false -> Some(state.copy(isInitialized = true)) toT Some(EricAction.InitializeAction(command.getEricScope))
                true -> Some(state) toT None
            }
        })

private fun interpretDismissSnackBarCommand(interpreterState: Option<EricInterpreterState>)
        : Tuple2<Option<EricInterpreterState>, Option<EricAction>> =
        interpreterState toT Some(EricAction.DismissSnackBarAction)


private fun interpretEmailEricCommand(interpreterState: Option<EricInterpreterState>)
        : Tuple2<Option<EricInterpreterState>, Option<EricAction>> =
        interpreterState toT Some(EricAction.EmailEricAction)



