package com.publicmethod.ericdewildt.ui.eric.archer.algebras

import com.publicmethod.archer.Action
import com.publicmethod.ericdewildt.scopes.GetEricScope

sealed class EricAction : Action {
    data class InitializeAction(val getEricScope: GetEricScope) : EricAction()
    object EmailEricAction : EricAction()
    object DismissSnackBarAction : EricAction()
}