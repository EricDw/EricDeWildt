package com.publicmethod.archer.states

import com.publicmethod.archer.Archer

const val DEFAULT_TEST_STRING = "DEFAULT"

data class TestInterpreterState(val text: String = DEFAULT_TEST_STRING): Archer.State