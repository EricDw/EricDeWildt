package com.publicmethod.ericdewildt.ui.eric

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.publicmethod.ericdewildt.data.TestLeftEricRepository
import com.publicmethod.ericdewildt.data.TestRightEricRepository
import com.publicmethod.ericdewildt.scopes.Scopes.GetEricScope
import com.publicmethod.ericdewildt.threading.TestContextProvider
import com.publicmethod.ericdewildt.ui.eric.mvc.EricViewModel
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricCommand.InitializeCommand
import com.publicmethod.ericdewildt.ui.eric.mvc.algebras.EricState
import com.publicmethod.kotlintestingutils.assertTrueWithMessage
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EricViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: EricViewModel

    @Before
    fun setUp() {
        viewModel = EricViewModel(TestContextProvider())
    }

    @Test
    fun given_Right_InitializeCommand_return_ShowEricState() {
        // Arrange
        val input = InitializeCommand(GetEricScope(TestRightEricRepository()))
        val expectedOutput = true

        // Act
        viewModel.issueCommand(input)
        val actualOutput = viewModel.state.value!! is EricState.ShowEricState

        // Assert
        assertTrueWithMessage(input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput)
    }

    @Test
    fun given_Left_InitializeCommand_return_ShowErrorState() {
        // Arrange
        val input = InitializeCommand(GetEricScope(TestLeftEricRepository()))
        val expectedOutput = true

        // Act
        viewModel.issueCommand(input)
        val actualOutput = viewModel.state.value!! is EricState.ShowErrorState

        // Assert
        assertTrueWithMessage(input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput)
    }

}