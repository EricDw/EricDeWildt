package com.publicmethod.ericdewildt.ui.eric.bow.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricResult.*
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricState
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun reduceEricResult(
    result: EricResult,
    stateChannel: SendChannel<EricState>
): State<Option<EricState>, Option<EricState>> =
    State { optionalState ->
        optionalState.fold({
            None toT None
        }, { someState ->
            return@State with(
                when (result) {
                    is InitializeResult ->
                        reducedInitializeResult(
                            result,
                            someState
                        )

                    is ShowLoadingResult ->
                        reducedShowLoadingResult(
                            someState
                        )

                    is EmailEricResult ->
                        reducedEmailEricResult(
                            someState
                        )

                    is DismissSnackBarResult ->
                        reducedDismissSnackBarResult(
                            someState
                        )
                }
            ) {
                launch(someState.backgroundContext) {
                    stateChannel.send(this@with)
                }

                Some(this) toT Some(this)
            }
        })
    }

fun reducedInitializeResult(
    result: InitializeResult,
    state: EricState
): EricState =
    result.eric.fold(
        { error ->
            state.copy(error = Option.fromNullable(error))
        },
        { eric ->
            state.copy(eric = eric.some())
        })

fun reducedShowLoadingResult(
    state: EricState
): EricState =
    state.copy(isLoading = true)

fun reducedEmailEricResult(
    state: EricState
): EricState =
    state.copy(showSnackBar = true)

fun reducedDismissSnackBarResult(
    state: EricState
): EricState =
    state.copy(showSnackBar = false)