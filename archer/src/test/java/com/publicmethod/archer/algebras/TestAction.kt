package com.publicmethod.archer.algebras

import com.publicmethod.archer.Action

sealed class TestAction: Action {
    object RightAction: TestAction()
    object LeftAction: TestAction()
    object WorkerAction: TestAction()
}