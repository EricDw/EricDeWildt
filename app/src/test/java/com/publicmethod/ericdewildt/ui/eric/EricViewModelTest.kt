package com.publicmethod.ericdewildt.ui.eric

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import arrow.core.None
import arrow.core.Some
import com.publicmethod.ericdewildt.data.TestLeftEricRepository
import com.publicmethod.ericdewildt.data.TestRightEricRepository
import com.publicmethod.ericdewildt.scopes.GetEricScope
import com.publicmethod.ericdewildt.threading.TestContextProvider
import com.publicmethod.ericdewildt.ui.eric.bow.EricViewModel
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand
import com.publicmethod.ericdewildt.ui.eric.bow.algebras.EricCommand.InitializeCommand
import com.publicmethod.ericdewildt.ui.eric.bow.states.EricState
import com.publicmethod.kotlintestingutils.assertTrueWithMessage
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EricViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: EricViewModel
    private lateinit var state: EricState

    @Before
    fun setUp() {
        viewModel = EricViewModel(
            TestContextProvider().backgroundContext()
        )
        viewModel.state.observeForever { state = it }
    }

    @Test
    fun given_Right_InitializeCommand_return_Some_Eric() =
        runBlocking(Unconfined) {
            // Arrange
            val input = InitializeCommand(GetEricScope(TestRightEricRepository()))
            val expectedOutput = true

            // Act
            viewModel.send(input)
            delay(100)
            val actualOutput = state.eric is Some
            // Assert
            assertTrueWithMessage(
                input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput
            )
        }

    @Test
    fun given_Left_InitializeCommand_return_None_Eric() =
        runBlocking(Unconfined) {
            // Arrange
            val input = InitializeCommand(GetEricScope(TestLeftEricRepository()))
            val expectedOutput = None

            // Act
            viewModel.send(input)
            delay(100)
            val actualOutput = state.eric

            // Assert
            assertTrueWithMessage(
                input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput
            )
        }

    @Test
    fun given_EmailEricCommand_return_ShowSnackBar_is_true() =
        runBlocking(Unconfined) {
            // Arrange
            val input = EricCommand.EmailEricCommand
            val expectedOutput = true

            // Act
            viewModel.send(input)
            delay(100)
            val actualOutput = state.showSnackBar

            // Assert
            assertTrueWithMessage(
                input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput
            )
        }

}