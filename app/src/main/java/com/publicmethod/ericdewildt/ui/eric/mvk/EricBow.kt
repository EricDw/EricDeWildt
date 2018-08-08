package com.publicmethod.ericdewildt.ui.eric.mvk

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricState
import kotlinx.coroutines.experimental.Job

class EricBow(contextProvider: ContextProvider = ContextProvider())
    : Archer.Bow<EricAction, EricResult, EricKommand, EricState>(
        Job(),
        contextProvider::backgroundContext,
        ::ericReducer,
        ::ericProcessor,
        ::ericInterpreter
)
