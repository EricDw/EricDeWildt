package com.publicmethod.ericdewildt.ui.eric.svc.algebras

import com.publicmethod.archer.Archer.Command
import com.publicmethod.ericdewildt.scopes.Scopes.GetEricScope

sealed class EricCommand : Command {
    data class InitializeCommand(val getEricScope: GetEricScope) : EricCommand()
    object EmailEricCommand : EricCommand()
    object DismissSnackBarCommand : EricCommand()
}