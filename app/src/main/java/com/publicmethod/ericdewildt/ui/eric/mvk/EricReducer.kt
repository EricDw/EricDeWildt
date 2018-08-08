package com.publicmethod.ericdewildt.ui.eric.mvk

import arrow.core.Option
import arrow.core.some
import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricState
import kotlinx.coroutines.experimental.channels.SendChannel

fun ericReducer(): Archer.Reducer<EricResult, EricState> =
        object : Archer.Reducer<EricResult, EricState> {

            private var ericModel = EricModel()

            override suspend fun reduce(result: EricResult, stateChannel: SendChannel<EricState>) {
                when (result) {
                    is EricResult.InitializeResult -> reduceInitializeResult(
                            result,
                            stateChannel
                    )

                    is EricResult.ShowLoadingResult -> reduceShowLoadingResult(
                            result,
                            stateChannel
                    )

                    is EricResult.EmailEricResult -> reduceEmailEricResult(
                            result,
                            stateChannel
                    )

                    is EricResult.DismissSnackBarResult -> reduceDismissSnackBarResult(
                            result,
                            stateChannel
                    )

                }
            }

            private suspend fun reduceShowLoadingResult(
                    result: EricResult.ShowLoadingResult,
                    stateChannel: SendChannel<EricState>
            ) {

                ericModel = ericModel.copy(isLoading = true)
                stateChannel.send(EricState.ShowLoadingState(ericModel))
            }

            private suspend fun reduceInitializeResult(
                    result: EricResult.InitializeResult,
                    stateChannel: SendChannel<EricState>
            ) {
                lateinit var newModel: EricModel
                lateinit var state: EricState

                result.eric.fold(
                        { error ->
                            newModel = ericModel.copy(error =
                            Option.fromNullable(error))
                            state = EricState.ShowErrorState(newModel)
                        },
                        { eric ->
                            newModel = ericModel.copy(eric = eric.some())
                            state = EricState.ShowEricState(newModel)
                        })

                ericModel = newModel
                stateChannel.send(state)

            }

            private suspend fun reduceEmailEricResult(
                    result: EricResult.EmailEricResult,
                    stateChannel: SendChannel<EricState>
            ) {
                stateChannel.send(EricState.ShowEmailEricState)
            }

            private suspend fun reduceDismissSnackBarResult(
                    result: EricResult,
                    stateChannel: SendChannel<EricState>
            ) {
                ericModel = ericModel.copy(showSnackBar = false)
                stateChannel.send(EricState.DismissSnackBar(ericModel))
            }
        }