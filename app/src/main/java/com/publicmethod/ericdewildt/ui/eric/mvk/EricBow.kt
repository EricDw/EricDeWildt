package com.publicmethod.ericdewildt.ui.eric.mvk

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricState
import kotlinx.coroutines.experimental.Job

class EricBow(contextProvider: ContextProvider = ContextProvider())
    : Archer.Bow<EricModel, EricAction, EricResult, EricKommand, EricState>(
        contextProvider.backgroundContext,
        contextProvider.uiContext,
        EricModel(),
        Job(),
        ::ericInterpreter,
        ::ericProcessor,
        ::ericReduction)
