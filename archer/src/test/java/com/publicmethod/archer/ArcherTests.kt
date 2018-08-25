package com.publicmethod.archer

import arrow.core.Option
import arrow.core.Some
import arrow.core.toT
import arrow.data.Reader
import arrow.data.State
import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.states.TestReducerState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ArcherTests {

    private lateinit var archer: Archer<TestCommand, TestReducerState>
    private lateinit var parent: Job

    @Before
    fun setUp() {
        parent = Job()
        archer = archer<TestAction, TestResult, TestCommand, TestReducerState>(
            Unconfined,
            parent,
            Some(TestReducerState()),
            Reader { (command, _) ->
                when (command) {
                    TestCommand.RightCommand -> Some(TestAction.RightAction)
                    TestCommand.LeftCommand -> Some(TestAction.LeftAction)
                }.toId()
            },
            Reader { (action, returnChannel) ->
                when (action) {
                    TestAction.RightAction -> Some(TestResult.RightResult)
                    TestAction.LeftAction -> Some(TestResult.LeftResult)
                    TestAction.WorkerAction -> {
                        launch(Unconfined) {
                            returnChannel.send(TestResult.RightResult)
                        }
                        Some(TestResult.WorkerResult)
                    }
                }.toId()
            },
            Reader { (result, _) ->
                State<Option<TestReducerState>, Option<TestReducerState>> {
                    when (result) {
                        TestResult.RightResult -> {
                            val state = Some(TestReducerState(REDUCED_RIGHT))
                            state toT state
                        }
                        TestResult.LeftResult -> {
                            val state = Some(TestReducerState(REDUCED_LEFT))
                            state toT state
                        }
                        TestResult.WorkerResult -> {
                            val state = Some(TestReducerState(REDUCED_WORKER))
                            state toT state
                        }
                    }
                }.toId()
            }
        )
    }

    @Test
    fun given_TestCommand_Right_receive_TestStateRight() = runBlocking {
        //        Arrange
        val input = TestCommand.RightCommand
        val expected = TestReducerState(REDUCED_RIGHT)

//        Act
        archer.commandChannel().send(input)
        val actual = archer.stateChannel().receive()

//        Assert
        Assert.assertEquals(expected, actual)

    }

    @Test
    fun given_TestCommand_Left_receive_TestStateLeft() = runBlocking {
        //        Arrange
        val input = TestCommand.LeftCommand
        val expected = TestReducerState(REDUCED_LEFT)

//        Act
        archer.commandChannel().send(input)
        val actual = archer.stateChannel().receive()

//        Assert
        Assert.assertEquals(expected, actual)

    }
}