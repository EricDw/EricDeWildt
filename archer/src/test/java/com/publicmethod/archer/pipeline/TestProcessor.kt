package com.publicmethod.archer.pipeline

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.*
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestProcessorState
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun processTestAction(
        action: TestAction,
        reducer: SendChannel<TestResult>
): State<Option<TestProcessorState>, Option<TestProcessorState>> =
        State { processorState ->
            processorState.fold({
                None toT None
            }, { state ->
                return@State with(when (action) {

                    is TestAction.RightAction ->
                        processRight(
                                state
                        )

                    is TestAction.LeftAction ->
                        processLeft(
                                state
                        )

                    is TestAction.WorkerAction ->
                        processWorker(
                                state,
                                reducer
                        )

                }) {
                    launch(Unconfined) {
                        reducer.send(second)
                    }
                    Some(first) toT Some(first)
                }
            })
        }

fun processRight(
        processorState: TestProcessorState
): Pair<TestProcessorState, TestResult> =
        processorState.copy(text = PROCESSED_RIGHT) to TestResult.RightResult

fun processLeft(
        processorState: TestProcessorState
): Pair<TestProcessorState, TestResult> =
        processorState.copy(text = PROCESSED_LEFT) to TestResult.LeftResult

fun processWorker(
        processorState: TestProcessorState,
        returnChannel: SendChannel<TestResult>
): Pair<TestProcessorState, TestResult> {
    launch(Unconfined) {
        val work: Work = WORKER_KEY to suspend {
            launch(Unconfined) {
                returnChannel.send(TestResult.WorkerResult)
            }
        }
        processorState.worker.startOrRestartWork(
                listOf(work)
        )
    }
    return processorState.copy(text = PROCESSED_RIGHT) to TestResult.RightResult
}
