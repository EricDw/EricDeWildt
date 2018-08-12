package com.publicmethod.archer.algebras

import com.publicmethod.archer.Archer

sealed class TestResult : Archer.Result {
    object RightResult : TestResult()
    object LeftResult : TestResult()
    object WokerResult : TestResult()
}