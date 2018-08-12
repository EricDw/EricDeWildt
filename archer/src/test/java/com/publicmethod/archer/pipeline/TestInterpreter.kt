package com.publicmethod.archer.pipeline

import arrow.core.Option
import arrow.core.some
import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.INTERPRETED_LEFT
import com.publicmethod.archer.INTERPRETED_RIGHT
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.states.TestInterpreterState

fun interpretTestCommand(command: TestCommand)
        : State<TestInterpreterState, Option<TestAction>> =
        when (command) {
            TestCommand.RightCommand -> {
                State {
                    it.copy(text = INTERPRETED_RIGHT) toT TestAction.RightAction.some()
                }
            }
            TestCommand.LeftCommand -> State {
                it.copy(text = INTERPRETED_LEFT) toT TestAction.LeftAction.some()
            }
        }