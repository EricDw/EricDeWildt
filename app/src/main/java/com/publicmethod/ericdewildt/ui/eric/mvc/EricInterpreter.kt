package com.publicmethod.ericdewildt.ui.eric.mvc

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricAction.InitializeAction
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricCommand.InitializeCommand

object EricInterpreter : Archer.CommandInterpreter<EricCommand, EricAction>() {
    override fun interpretCommand(command: EricCommand): EricAction =
            when (command) {
                is InitializeCommand -> interpretInitializeCommand(command)
            }

    private fun interpretInitializeCommand(command: InitializeCommand): EricAction =
            InitializeAction(command.getEricScope)
}
