package com.publicmethod.archer.pipeline

import arrow.core.*
import arrow.data.Reader
import arrow.data.State
import com.publicmethod.archer.REDUCED_LEFT
import com.publicmethod.archer.REDUCED_RIGHT
import com.publicmethod.archer.REDUCED_WORK
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestReducerState
import com.publicmethod.archer.toId
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun reduceWork(
    result: TestResult.WorkerResult,
    reducerState: TestReducerState
): TestReducerState =
    result.work.fold({ TestReducerState() }, {
        launch(Unconfined) {
            it.head.jobFunction()
        }
        return reducerState.copy(text = REDUCED_WORK)
    })

fun reduceLeft(reducerState: TestReducerState) =
    reducerState.copy(text = REDUCED_LEFT)

fun reduceRight(reducerState: TestReducerState) =
    reducerState.copy(text = REDUCED_RIGHT)

fun testReducer(): Reader<Tuple2<TestResult, SendChannel<TestReducerState>>,
        State<Option<TestReducerState>, Option<TestReducerState>>> =
    Reader { (result, returnChannel) ->
        State<Option<TestReducerState>,
                Option<TestReducerState>> { optionState ->
            optionState.fold({ None toT None }, { nonOptionState ->
                return@State with(
                    when (result) {
                        is TestResult.RightResult ->
                            reduceRight(nonOptionState)

                        is TestResult.LeftResult ->
                            reduceLeft(nonOptionState)

                        is TestResult.WorkerResult ->
                            reduceWork(result, nonOptionState)
                    }
                ) {
                    launch(Unconfined) {
                        returnChannel.send(this@with)
                    }
                    Some(this) toT Some(this)
                }
            })
        }.toId()
    }