package com.publicmethod.ericdewildt.ui.eric.mvk.algebras

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.scopes.Scopes

sealed class EricAction : Archer.Action {
    data class InitializeAction(val getEricScope: Scopes.GetEricScope) : EricAction()
    object EmailEricAction : EricAction()
    object DismissSnackBarAction : EricAction()
}