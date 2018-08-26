package com.publicmethod.archer.pipeline

import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.toT
import arrow.data.Reader
import arrow.data.State
import com.publicmethod.archer.REDUCED_LEFT
import com.publicmethod.archer.REDUCED_RIGHT
import com.publicmethod.archer.REDUCED_WORK
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.algebras.TestCommand.*
import com.publicmethod.archer.states.TestReducerState
import com.publicmethod.archer.toId
import kotlinx.coroutines.experimental.channels.SendChannel

typealias CommandChannelTuple<C> = Tuple2<C, SendChannel<C>>
typealias OptionState<S> = State<Option<S>, Option<S>>

fun testPipeline() =
    Reader<CommandChannelTuple<TestCommand>, OptionState<TestReducerState>> { tuple ->
        State<Option<TestReducerState>, Option<TestReducerState>> { optionState ->
            when (tuple.a) {
                RightCommand -> {
                    val newState = optionState.map { state ->
                        state.copy(text = REDUCED_RIGHT)
                    }
                    newState toT newState
                }

                LeftCommand -> {
                    val newState = optionState.map { state ->
                        state.copy(text = REDUCED_LEFT)
                    }
                    newState toT newState
                }
                TestCommand.WorkCommand -> {
                    val newState = optionState.map { state ->
                        state.copy(text = REDUCED_WORK)
                    }
                    newState toT newState
                }
            }

        }.toId()
    }