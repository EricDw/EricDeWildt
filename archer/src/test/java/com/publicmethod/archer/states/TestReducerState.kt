package com.publicmethod.archer.states

import com.publicmethod.archer.Archer
import com.publicmethod.archer.DEFAULT_TEST_STRING

data class TestReducerState(val text: String = DEFAULT_TEST_STRING) : Archer.State