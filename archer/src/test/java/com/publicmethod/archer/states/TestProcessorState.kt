package com.publicmethod.archer.states

import com.publicmethod.archer.DEFAULT_TEST_STRING
import com.publicmethod.archer.FunctionWorker
import com.publicmethod.archer.StateData

data class TestProcessorState(val text: String = DEFAULT_TEST_STRING,
                              val worker: FunctionWorker) : StateData