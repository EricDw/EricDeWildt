package com.publicmethod.archer

import arrow.core.Some
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.pipeline.testInterpreter
import com.publicmethod.archer.pipeline.testProcessor
import com.publicmethod.archer.pipeline.testReducer
import com.publicmethod.archer.states.TestReducerState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.Unconfined
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
        archer = archer(
            Unconfined,
            parent,
            Some(TestReducerState()),
            testInterpreter(),
            testProcessor(),
            testReducer()
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

    @Test
    fun given_TestCommand_Worker_receive_TestStateWorker() = runBlocking {
        //        Arrange
        val input = TestCommand.WorkCommand
        val expected = TestReducerState(REDUCED_WORK)

//        Act
        archer.commandChannel().send(input)
        val actual = archer.stateChannel().receive()

//        Assert
        Assert.assertEquals(expected, actual)

    }
}