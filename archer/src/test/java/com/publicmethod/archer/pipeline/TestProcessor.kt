package com.publicmethod.archer.pipeline

import arrow.core.Option
import arrow.core.some
import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.Archer.FunctionWorkerMessage.*
import com.publicmethod.archer.PROCESSED_LEFT
import com.publicmethod.archer.PROCESSED_RIGHT
import com.publicmethod.archer.WORKER_KEY
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestProcessorState
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

fun processTestAction(action: TestAction, reducer: SendChannel<TestResult>)
        : State<TestProcessorState, Option<TestResult>> =
        when (action) {
            is TestAction.RightAction -> State {
                it.copy(text = PROCESSED_RIGHT) toT TestResult.RightResult.some()
            }

            is TestAction.LeftAction -> State {
                it.copy(text = PROCESSED_LEFT) toT TestResult.LeftResult.some()
            }

            is TestAction.WorkerAction -> State {
                launch(Unconfined) {
                    with(it.worker) {
                        send(AddWork(WORKER_KEY) {
                            reducer.send(TestResult.WokerResult)
                        })
                        send(StartOrRestartWork(WORKER_KEY))
                    }
                }
                it.copy(text = PROCESSED_RIGHT) toT TestResult.RightResult.some()

            }
        }
