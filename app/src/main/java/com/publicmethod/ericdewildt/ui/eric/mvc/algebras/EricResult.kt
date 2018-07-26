package com.publicmethod.ericdewildt.ui.eric.mvc.algebras

import arrow.core.Either
import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.algebras.EricError
import com.publicmethod.ericdewildt.remote.algebras.ApiError

sealed class EricResult : Archer.Result {
    data class InitializeResult(val eric: Either<EricError, Eric>) : EricResult()
}