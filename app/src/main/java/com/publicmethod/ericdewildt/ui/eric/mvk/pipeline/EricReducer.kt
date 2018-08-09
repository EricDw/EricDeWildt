package com.publicmethod.ericdewildt.ui.eric.mvk.pipeline

import arrow.core.Option
import arrow.core.some
import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.ui.eric.mvk.EricState
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricResult.*
import kotlinx.coroutines.experimental.channels.SendChannel

fun ericReducer(): Archer.Reducer<EricResult, EricState> =
        object : Archer.Reducer<EricResult, EricState> {

            private var ericState = EricState()

            override suspend fun reduce(
                    result: EricResult,
                    stateChannel: SendChannel<EricState>
            ) {
                ericState = when (result) {
                    is InitializeResult ->
                        reduceInitializeResult(result)

                    is ShowLoadingResult ->
                        reduceShowLoadingResult()

                    is EmailEricResult ->
                        reduceEmailEricResult()

                    is DismissSnackBarResult ->
                        reduceDismissSnackBarResult()
                }
                stateChannel.send(ericState)
            }

            private fun reduceShowLoadingResult(): EricState =
                    ericState.copy(isLoading = true)

            private fun reduceInitializeResult(
                    result: InitializeResult
            ): EricState =
                    result.eric.fold(
                            { error ->
                                ericState.copy(error = Option.fromNullable(error))
                            },
                            { eric ->
                                ericState.copy(eric = eric.some())
                            })

            private fun reduceEmailEricResult(): EricState =
                    ericState.copy(showSnackBar = true)

            private fun reduceDismissSnackBarResult(): EricState =
                    ericState.copy(showSnackBar = false)

        }