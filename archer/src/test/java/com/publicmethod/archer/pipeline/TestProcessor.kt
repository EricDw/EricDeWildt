package com.publicmethod.archer.pipeline

import arrow.core.*
import arrow.data.State
import com.publicmethod.archer.PROCESSED_LEFT
import com.publicmethod.archer.PROCESSED_RIGHT
import com.publicmethod.archer.WORKER_KEY
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.startOrRestartWork
import com.publicmethod.archer.states.TestProcessorState
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun processTestAction(
        action: TestAction,
        reducer: Option<SendChannel<TestResult>>
): State<Option<TestProcessorState>, Option<TestResult>> =
        State { internalState ->
            when (action) {
                is TestAction.RightAction ->
                    processRight(internalState)

                is TestAction.LeftAction ->
                    processLeft(internalState)

                is TestAction.WorkerAction ->
                    processWorker(reducer, internalState)
            }
        }

private fun processWorker(
        returnChannel: Option<SendChannel<TestResult>>,
        internalState: Option<TestProcessorState>
): Tuple2<Option<TestProcessorState>, Option<TestResult>> =
        returnChannel.fold({
            internalState toT None
        }, { rc ->
            internalState.fold({
                None toT None
            }, { state ->
                launch(Unconfined) {
                    val workerJob = WORKER_KEY to suspend {
                        launch(Unconfined) {
                            rc.send(TestResult.WorkerResult)
                        }
                    }
                    state.worker.startOrRestartWork(
                            listOf(workerJob)
                    )
                }
                Some(state.copy(text = PROCESSED_RIGHT)) toT TestResult.RightResult.some()
            })
        })

private fun processLeft(internalState: Option<TestProcessorState>) =
        internalState.fold({
            None toT Some(TestResult.LeftResult)
        }, { state ->
            Some(state.copy(text = PROCESSED_LEFT)) toT Some(TestResult.LeftResult)
        })

private fun processRight(internalState: Option<TestProcessorState>) =
        internalState.fold({
            None toT Some(TestResult.RightResult)
        }, { state ->
            Some(state.copy(text = PROCESSED_RIGHT)) toT Some(TestResult.RightResult)
        })