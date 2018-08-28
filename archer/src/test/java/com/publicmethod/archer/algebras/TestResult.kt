package com.publicmethod.archer.algebras

import arrow.core.Option
import arrow.data.Nel
import com.publicmethod.archer.Result
import com.publicmethod.archer.Work

sealed class TestResult : Result {
    object RightResult : TestResult()
    object LeftResult : TestResult()
    data class WorkerResult(val work: Option<Nel<Work>>) : TestResult()
}