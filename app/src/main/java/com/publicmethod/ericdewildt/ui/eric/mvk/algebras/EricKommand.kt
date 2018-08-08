package com.publicmethod.ericdewildt.ui.eric.mvk.algebras

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.scopes.Scopes.GetEricScope

sealed class EricKommand : Archer.Kommand {
    data class InitializeKommand(val getEricScope: GetEricScope) : EricKommand()
    object EmailEricKommand : EricKommand()
    object DismissSnackBarKommand : EricKommand()
}