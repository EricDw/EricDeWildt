package com.publicmethod.archer.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.archer.REDUCED_LEFT
import com.publicmethod.archer.REDUCED_RIGHT
import com.publicmethod.archer.REDUCED_WORKER
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestReducerState
import kotlinx.coroutines.experimental.channels.SendChannel

fun reduceTestResult(
        result: TestResult,
        stateChannel: Option<SendChannel<TestReducerState>>
): State<Option<TestReducerState>, Option<TestReducerState>> =
        State { internalState ->
            when (result) {
                TestResult.RightResult ->
                    reduceRight(internalState)

                TestResult.LeftResult ->
                    reduceLeft(internalState)

                TestResult.WorkerResult ->
                    reduceWorker(internalState)
            }
        }

private fun reduceWorker(internalState: Option<TestReducerState>)
        : Tuple2<Option<TestReducerState>, Option<TestReducerState>> =
        internalState.fold({
            None toT None

        }, { state ->
            val newState = Some(state.copy(text = REDUCED_WORKER))
            newState toT newState
        })


private fun reduceLeft(internalState: Option<TestReducerState>)
        : Tuple2<Option<TestReducerState>, Option<TestReducerState>> =
        internalState.fold({
            None toT None

        }, { state ->
            val newState = Some(state.copy(text = REDUCED_LEFT))
            newState toT newState
        })

private fun reduceRight(internalState: Option<TestReducerState>)
        : Tuple2<Option<TestReducerState>, Option<TestReducerState>> =
        internalState.fold({
            None toT None

        }, { state ->
            val newState = Some(state.copy(text = REDUCED_RIGHT))
            newState toT newState
        })