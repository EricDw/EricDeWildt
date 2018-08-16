package com.publicmethod.ericdewildt.ui.eric.bow.algebras

import com.publicmethod.archer.Command
import com.publicmethod.ericdewildt.scopes.GetEricScope

sealed class EricCommand : Command {
    data class InitializeCommand(
        val getEricScope: GetEricScope
    ) : EricCommand()

    object EmailEricCommand : EricCommand()
    object DismissSnackBarCommand : EricCommand()
}