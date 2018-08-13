package com.publicmethod.archer.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.archer.INTERPRETED_LEFT
import com.publicmethod.archer.INTERPRETED_RIGHT
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.states.TestInterpreterStateData
import kotlinx.coroutines.experimental.channels.SendChannel

fun interpretTestCommand(
        command: TestCommand,
        processor: Option<SendChannel<TestAction>>
): State<Option<TestInterpreterStateData>, Option<TestAction>> =
        State { internalState ->
            when (command) {
                TestCommand.RightCommand ->
                    interpretRight(internalState)

                TestCommand.LeftCommand ->
                    interpretLeft(internalState)
            }
        }

private fun interpretLeft(internalState: Option<TestInterpreterStateData>) =
        internalState.fold({
            None toT TestAction.LeftAction.some()
        }, { inState ->
            Some(inState.copy(text = INTERPRETED_LEFT)) toT TestAction.LeftAction.some()
        })

private fun interpretRight(internalState: Option<TestInterpreterStateData>) =
        internalState.fold({
            None toT TestAction.RightAction.some()
        }, { inState ->
            Some(inState.copy(text = INTERPRETED_RIGHT)) toT TestAction.RightAction.some()
        })