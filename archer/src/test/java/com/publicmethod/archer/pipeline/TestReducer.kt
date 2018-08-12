package com.publicmethod.archer.pipeline

import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestReducerState

const val REDUCED_RIGHT = "Reduced Right"
const val REDUCED_LEFT = "Reduced Left"
const val REDUCED_WORKER = "Reduced Work"

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