package com.publicmethod.archer.pipeline

import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.REDUCED_LEFT
import com.publicmethod.archer.REDUCED_RIGHT
import com.publicmethod.archer.REDUCED_WORKER
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestReducerState

fun reduceTestResult(result: TestResult)
        : State<TestReducerState, TestReducerState> =
        when (result) {
            TestResult.RightResult -> State {
                val newState = it.copy(text = REDUCED_RIGHT)
                newState toT newState
            }

            TestResult.LeftResult -> State {
                val newState = it.copy(text = REDUCED_LEFT)
                newState toT newState
            }

            TestResult.WokerResult -> State {
                val newState = it.copy(text = REDUCED_WORKER)
                newState toT newState
            }
        }