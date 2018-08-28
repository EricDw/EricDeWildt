package com.publicmethod.archer.pipeline

import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.data.Reader
import com.publicmethod.archer.ActionChannel
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestAction.*
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.toId


fun testInterpreter(): Reader<Tuple2<TestCommand, ActionChannel<TestAction>>, Option<TestAction>> =
    Reader { (command, _) ->
        when (command) {
            TestCommand.RightCommand -> Some(RightAction)
            TestCommand.LeftCommand -> Some(LeftAction)
            TestCommand.WorkCommand -> Some(WorkerAction)
        }.toId()
    }

