package com.publicmethod.archer.states

import com.publicmethod.archer.Archer
import com.publicmethod.archer.FunctionWorker

data class TestProcessorState(val text: String = DEFAULT_TEST_STRING,
                              val worker: FunctionWorker): Archer.State