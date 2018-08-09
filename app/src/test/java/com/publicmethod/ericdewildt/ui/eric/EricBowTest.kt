package com.publicmethod.ericdewildt.ui.eric

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import arrow.core.None
import arrow.core.Some
import com.publicmethod.ericdewildt.data.TestLeftEricRepository
import com.publicmethod.ericdewildt.data.TestRightEricRepository
import com.publicmethod.ericdewildt.scopes.Scopes.GetEricScope
import com.publicmethod.ericdewildt.threading.TestContextProvider
import com.publicmethod.ericdewildt.ui.eric.mvk.EricBow
import com.publicmethod.ericdewildt.ui.eric.mvk.EricState
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand
import com.publicmethod.ericdewildt.ui.eric.mvk.algebras.EricKommand.InitializeKommand
import com.publicmethod.kotlintestingutils.assertTrueWithMessage
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EricBowTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: EricBow
    private lateinit var state: EricState

    @Before
    fun setUp() {
        viewModel = EricBow(TestContextProvider())
        viewModel.state.observeForever { state = it }
    }

    @Test
    fun given_Right_InitializeCommand_return_Some_Eric() {
        // Arrange
        val input = InitializeKommand(GetEricScope(TestRightEricRepository()))
        val expectedOutput = true

        // Act
        viewModel.issueKommand(input)
        val actualOutput = state.eric is Some

        // Assert
        assertTrueWithMessage(input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput)
    }

    @Test
    fun given_Left_InitializeCommand_return_None_Eric() {
        // Arrange
        val input = InitializeKommand(GetEricScope(TestLeftEricRepository()))
        val expectedOutput = true

        // Act
        viewModel.issueKommand(input)
        val actualOutput = state.eric is None

        // Assert
        assertTrueWithMessage(input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput)
    }

    @Test
    fun given_EmailEricCommand_return_ShowSnackBar_is_true() {
        // Arrange
        val input = EricKommand.EmailEricKommand
        val expectedOutput = true

        // Act
        viewModel.issueKommand(input)
        val actualOutput = state.showSnackBar

        // Assert
        assertTrueWithMessage(input = input,
                expectedOutput = expectedOutput,
                actualOutput = actualOutput)
    }

}