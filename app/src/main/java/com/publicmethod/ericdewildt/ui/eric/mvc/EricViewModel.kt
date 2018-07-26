package com.publicmethod.ericdewildt.ui.eric.mvc

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.publicmethod.archer.Archer
import com.publicmethod.archer.LightBow
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricState
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.EricModel

class EricViewModel(contextProvider: ContextProvider = ContextProvider())
    : LightBow<EricCommand, EricAction, EricResult, EricState, EricModel>(
        contextProvider.uiContext,
        contextProvider.backgroundContext
) {

    private val mutableState : MutableLiveData<EricState> = MutableLiveData()

    val state : LiveData<EricState>
            get() = mutableState

    override val interpreter: Archer.CommandInterpreter<EricCommand, EricAction> =
            EricInterpreter

    override val processor: Archer.ActionProcessor<EricAction, EricResult> =
            EricProcessor

    override val reducer: Archer.StateReducer<EricResult, EricModel, EricState> =
            EricReducer

    override fun handleState(state: EricState) {
        mutableState.value = state
    }
}
