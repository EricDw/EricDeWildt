package com.publicmethod.ericdewildt.ui.eric.bow.pipeline

import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.some
import arrow.data.Reader
import com.publicmethod.archer.ActionChannel
import com.publicmethod.archer.toId
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand.EmailEricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand.InitializeCommand

fun interpretEricCommand(): Reader<Tuple2<EricCommand, ActionChannel<EricAction>>,
        Option<EricAction>> = Reader { (command, _) ->
    when (command) {
        is EricCommand.InitializeCommand ->
            interpretInitializeCommand(command)

        is EmailEricCommand ->
            interpretEmailEricCommand()

        is EricCommand.DismissSnackBarCommand ->
            interpretDismissSnackBarCommand()
    }.some().toId()
}

private fun interpretInitializeCommand(
    command: InitializeCommand
) = EricAction.InitializeAction(command.getEricScope)

private fun interpretEmailEricCommand() = EricAction.EmailEricAction

private fun interpretDismissSnackBarCommand() = EricAction.DismissSnackBarAction
