package com.publicmethod.ericdewildt.ui.eric.svc.algebras

import arrow.core.Either
import com.publicmethod.archer.Archer
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.algebras.EricError

sealed class EricResult : Archer.Result {
    data class InitializeResult(val eric: Either<EricError, Eric>) : EricResult()
    object ShowLoadingResult : EricResult()
    object EmailEricResult : EricResult()
    object DismissSnackBarResult : EricResult()
}