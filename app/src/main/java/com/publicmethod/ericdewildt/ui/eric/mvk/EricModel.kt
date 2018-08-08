package com.publicmethod.ericdewildt.ui.eric.mvk

import arrow.core.None
import arrow.core.Option
import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.algebras.EricError

data class EricModel(val eric: Option<Eric> = None,
                     val error: Option<EricError> = None,
                     val isLoading: Boolean = false,
                     val showSnackBar: Boolean = false) : Archer.Model

