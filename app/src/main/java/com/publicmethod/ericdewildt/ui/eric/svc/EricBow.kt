package com.publicmethod.ericdewildt.ui.eric.svc

import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.svc.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.svc.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.svc.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.svc.pipeline.ericInterpreter
import com.publicmethod.ericdewildt.ui.eric.svc.pipeline.ericProcessor
import com.publicmethod.ericdewildt.ui.eric.svc.pipeline.ericReducer
import kotlinx.coroutines.experimental.Job

class EricBow(contextProvider: ContextProvider = ContextProvider())
    : Archer.Bow<EricAction, EricResult, EricCommand, EricState>(
        Job(),
        contextProvider::backgroundContext,
        ::ericReducer,
        ::ericProcessor,
        ::ericInterpreter
)
