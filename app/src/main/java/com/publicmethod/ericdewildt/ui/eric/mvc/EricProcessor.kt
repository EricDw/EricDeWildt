package com.publicmethod.ericdewildt.ui.eric.mvc

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricAction.InitializeAction
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricResult.InitializeResult

object EricProcessor : Archer.ActionProcessor<EricAction, EricResult>() {
    override fun processAction(action: EricAction): EricResult =
            when (action) {
                is InitializeAction -> processInitializeAction(action)
            }

    private fun processInitializeAction(action: InitializeAction): EricResult =
            InitializeResult(action.getEricScope.ericRepository.getItem())

}
