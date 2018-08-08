package com.publicmethod.ericdewildt.ui.eric.mvk.algebras

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.EricModel

sealed class EricState : Archer.State {
    data class ShowErrorState(val ericModel: EricModel) : EricState()
    data class ShowEricState(val ericModel: EricModel) : EricState()
    data class ShowLoadingState(val ericModel: EricModel) : EricState()
    data class DismissSnackBar(val ericModel: EricModel) : EricState()

    object ShowEmailEricState : EricState()
}