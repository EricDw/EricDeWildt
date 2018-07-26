package com.publicmethod.ericdewildt.ui.eric.mvc.algebras

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.scopes.Scopes.GetEricScope

sealed class EricCommand : Archer.Command {
    data class InitializeCommand(val getEricScope: GetEricScope) : EricCommand()
}