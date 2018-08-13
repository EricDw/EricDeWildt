package com.publicmethod.archer.states

import com.publicmethod.archer.DEFAULT_TEST_STRING
import com.publicmethod.archer.StateData


data class TestInterpreterStateData(val text: String = DEFAULT_TEST_STRING) : StateData