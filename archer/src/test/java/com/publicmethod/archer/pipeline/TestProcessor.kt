package com.publicmethod.archer.pipeline

import arrow.core.Option
import arrow.core.some
import arrow.core.toT
import arrow.data.State
import com.publicmethod.archer.Archer.FunctionWorkerMessage.*
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestProcessorState
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

const val PROCESSED_RIGHT = "Processed Right"
const val PROCESSED_LEFT = "Processed Left"
const val WORKER_KEY = "work_key"

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
