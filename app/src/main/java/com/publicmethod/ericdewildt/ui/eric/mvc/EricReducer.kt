package com.publicmethod.ericdewildt.ui.eric.mvc

import arrow.core.Option
import arrow.core.some
import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.EricModel
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricState

object EricReducer : Archer.StateReducer<EricResult, EricModel, EricState>(
        EricModel()) {

    override fun reduceState(result: EricResult): arrow.data.State<EricModel, EricState> =
            when (result) {
                is EricResult.InitializeResult -> reduceInitializeState(result)
            }

    private fun reduceInitializeState(result: EricResult.InitializeResult)
            : State<EricModel, EricState> =
            State { oldModel ->
                lateinit var newModel: EricModel
                lateinit var state: EricState

                result.eric.fold(
                        { error ->
                            newModel = oldModel.copy(error =
                            Option.fromNullable(error))
                            state = EricState.ShowErrorState(newModel)
                        },
                        { eric ->
                            newModel = oldModel.copy(eric = eric.some())
                            state = EricState.ShowEricState(newModel)
                        })

                newModel toT state
            }
}
