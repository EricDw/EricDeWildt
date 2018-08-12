package com.publicmethod.archer.algebras

import com.publicmethod.archer.Archer

sealed class TestAction: Archer.Action {
    object RightAction: TestAction()
    object LeftAction: TestAction()
    object WorkerAction: TestAction()
}