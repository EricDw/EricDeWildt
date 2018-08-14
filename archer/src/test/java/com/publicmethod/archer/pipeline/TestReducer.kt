package com.publicmethod.archer.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.archer.REDUCED_LEFT
import com.publicmethod.archer.REDUCED_RIGHT
import com.publicmethod.archer.REDUCED_WORKER
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestReducerState
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun reduceTestResult(
        result: TestResult,
        stateChannel: SendChannel<TestReducerState>
): State<Option<TestReducerState>, Option<TestReducerState>> =
        State { internalState ->
            internalState.fold({ None toT None }, { state ->
                return@State with(when (result) {
                    TestResult.RightResult ->
                        reduceRight(state)

                    TestResult.LeftResult ->
                        reduceLeft(state)

                    TestResult.WorkerResult ->
                        reduceWorker(state)
                }) {
                    launch(Unconfined) {
                        stateChannel.send(this@with)
                    }
                    Some(this) toT Some(this)
                }
            })
        }

fun reduceWorker(reducerState: TestReducerState) =
        reducerState.copy(text = REDUCED_WORKER)

fun reduceLeft(reducerState: TestReducerState) =
        reducerState.copy(text = REDUCED_LEFT)

fun reduceRight(reducerState: TestReducerState) =
        reducerState.copy(text = REDUCED_RIGHT)