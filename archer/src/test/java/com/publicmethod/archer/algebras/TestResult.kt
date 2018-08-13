package com.publicmethod.archer.algebras

import com.publicmethod.archer.Result

sealed class TestResult : Result {
    object RightResult : TestResult()
    object LeftResult : TestResult()
    object WorkerResult : TestResult()
}