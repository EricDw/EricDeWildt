package com.publicmethod.ericdewildt.ui.eric.mvc.algebras

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.EricModel

sealed class EricState : Archer.State {
    data class ShowErrorState(val ericModel: EricModel) : EricState()
    data class ShowEricState(val ericModel: EricModel) : EricState()
}