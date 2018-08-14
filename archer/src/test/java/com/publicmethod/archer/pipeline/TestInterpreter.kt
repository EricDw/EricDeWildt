package com.publicmethod.archer.pipeline

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.INTERPRETED_LEFT
import com.publicmethod.archer.INTERPRETED_RIGHT
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.states.TestInterpreterState
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun interpretTestCommand(
        command: TestCommand,
        processor: SendChannel<TestAction>
): State<Option<TestInterpreterState>, Option<TestInterpreterState>> =
        State { interpreterState ->
            interpreterState.fold({
                None toT None
            }, { state ->
                return@State with(when (command) {
                    TestCommand.RightCommand ->
                        interpretRight(
                                state
                        )

                    TestCommand.LeftCommand ->
                        interpretLeft(
                                state
                        )
                }) {
                    launch(Unconfined) {
                        processor.send(second)
                    }
                    Some(first) toT Some(first)
                }
            })
        }

private fun interpretLeft(
        interpreterState: TestInterpreterState
): Pair<TestInterpreterState, TestAction.LeftAction> =
        interpreterState.copy(text = INTERPRETED_LEFT) to TestAction.LeftAction

private fun interpretRight(
        interpreterState: TestInterpreterState
): Pair<TestInterpreterState, TestAction.RightAction> =
        interpreterState.copy(text = INTERPRETED_RIGHT) to TestAction.RightAction