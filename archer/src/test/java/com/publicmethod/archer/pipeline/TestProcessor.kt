package com.publicmethod.archer.pipeline

import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.data.Nel
import arrow.data.Reader
import com.publicmethod.archer.*
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestResult
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.launch

fun testProcessor(): Reader<Tuple2<TestAction, ResultChannel<TestResult>>, Option<TestResult>> =
    Reader { (action, returnChannel) ->
        when (action) {
            TestAction.RightAction -> Some(TestResult.RightResult)
            TestAction.LeftAction -> Some(TestResult.LeftResult)
            TestAction.WorkerAction -> {
                val work: Work = WORK_KEY to suspend {
                    launch(Unconfined) {
                        returnChannel.send(TestResult.RightResult)
                    }
                }
                Some(TestResult.WorkerResult(Some(Nel(work))))
            }
        }.toId()
    }




