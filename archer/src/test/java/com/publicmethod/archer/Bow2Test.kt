package com.publicmethod.archer

import arrow.core.Option
import arrow.core.Some
import com.publicmethod.archer.algebras.TestCommand
import com.publicmethod.archer.algebras.TestCommand.LeftCommand
import com.publicmethod.archer.algebras.TestCommand.RightCommand
import com.publicmethod.archer.pipeline.testPipeline
import com.publicmethod.archer.states.TestReducerState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class Bow2Test {

    private lateinit var b2: SendChannel<TestCommand>
    private lateinit var stateChannel: Channel<Option<TestReducerState>>

    @Before
    fun setUp() {
        stateChannel = Channel()
        b2 = bow2(
            Unconfined,
            Channel.UNLIMITED,
            Job(),
            Some(TestReducerState()),
            stateChannel,
            testPipeline()
        )
    }

    @Test
    fun given_TestCommand_Right_receive_TestStateRight() = runBlocking {
        //        Arrange
        val input = RightCommand
        val expected = Some(TestReducerState(REDUCED_RIGHT))

//        Act
        b2.send(input)
        val actual = stateChannel.receive()

//        Assert
        assertEquals(expected, actual)

    }

    @Test
    fun given_TestCommand_Left_receive_TestStateLeft() = runBlocking {
        //        Arrange
        val input = LeftCommand
        val expected = Some(TestReducerState(REDUCED_LEFT))

//        Act
        b2.send(input)
        val actual = stateChannel.receive()

//        Assert
        assertEquals(expected, actual)

    }

}