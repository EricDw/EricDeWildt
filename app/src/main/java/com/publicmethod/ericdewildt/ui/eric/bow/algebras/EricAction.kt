package com.publicmethod.ericdewildt.ui.eric.bow.algebras

import com.publicmethod.archer.Action
import com.publicmethod.ericdewildt.scopes.Scopes

sealed class EricAction : Action {
    data class InitializeAction(val getEricScope: Scopes.GetEricScope) : EricAction()
    object EmailEricAction : EricAction()
    object DismissSnackBarAction : EricAction()
}