package com.publicmethod.archer.pipeline

import arrow.core.*
import arrow.data.Reader
import arrow.data.State
import com.publicmethod.archer.*
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestAction.*
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
            return@State with(
                when (command) {
                    TestCommand.RightCommand ->
                        interpretRight(
                            state
                        )

                    TestCommand.LeftCommand ->
                        interpretLeft(
                            state
                        )
                    TestCommand.WorkCommand ->
                        interpretWork(
                            state
                        )
                }
            ) {
                launch(Unconfined) {
                    processor.send(second)
                }
                Some(first) toT Some(first)
            }
        })
    }

fun interpretWork(state: TestInterpreterState): Pair<TestInterpreterState, TestAction> =
    state.copy(text = INTERPRETED_WORK) to WorkerAction


private fun interpretLeft(
    interpreterState: TestInterpreterState
): Pair<TestInterpreterState, LeftAction> =
    interpreterState.copy(text = INTERPRETED_LEFT) to LeftAction

private fun interpretRight(
    interpreterState: TestInterpreterState
): Pair<TestInterpreterState, TestAction.RightAction> =
    interpreterState.copy(text = INTERPRETED_RIGHT) to RightAction


fun testInterpreter(): Reader<Tuple2<TestCommand, ActionChannel<TestAction>>, Option<TestAction>> =
    Reader { (command, _) ->
        when (command) {
            TestCommand.RightCommand -> Some(RightAction)
            TestCommand.LeftCommand -> Some(LeftAction)
            TestCommand.WorkCommand -> Some(WorkerAction)
        }.toId()
    }

