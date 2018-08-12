package com.publicmethod.archer

import com.publicmethod.archer.algebras.TestAction
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.algebras.TestResult
import com.publicmethod.archer.pipeline.*
import com.publicmethod.archer.states.TestInterpreterState
import com.publicmethod.archer.states.TestProcessorState
import com.publicmethod.archer.states.TestReducerState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SVCMachineTests {

    private lateinit var machine: SVCMachine<TestAction, TestResult, TestCommand, TestReducerState>
    private lateinit var parent: Job

    @Before
    fun setUp() {
        parent = Job()
        machine = svcMachine(
                TestInterpreterState(),
                TestProcessorState(worker = Archer.functionWorker(parent, Unconfined)),
                TestReducerState(),
                ::interpretTestCommand,
                ::processTestAction,
                ::reduceTestResult,
                Unconfined,
                parent)
    }

    @Test
    fun given_TestCommand_Right_receive_TestStateRight() = runBlocking {
        //        Arrange
        val input = TestCommand.RightCommand
        val expected = TestReducerState(REDUCED_RIGHT)

//        Act
        machine.commandChannel().send(input)
        val actual = machine.stateChannel().receive()

//        Assert
        Assert.assertEquals(expected, actual)

    }

    @Test
    fun given_TestCommand_Left_receive_TestStateLeft() = runBlocking {
        //        Arrange
        val input = TestCommand.LeftCommand
        val expected = TestReducerState(REDUCED_LEFT)

//        Act
        machine.commandChannel().send(input)
        val actual = machine.stateChannel().receive()

//        Assert
        Assert.assertEquals(expected, actual)

    }

    @Test
    fun given_TestCommand_Worker_receive_TestStateWorker() = runBlocking {
        //        Arrange
        val input = TestAction.WorkerAction
        val expected = TestReducerState(REDUCED_WORKER)

//        Act
        machine.processorChannel().send(input)
        val actual = machine.stateChannel().receive()

//        Assert
        Assert.assertEquals(expected, actual)

    }
}