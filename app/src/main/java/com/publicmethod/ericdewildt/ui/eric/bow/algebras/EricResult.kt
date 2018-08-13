package com.publicmethod.ericdewildt.ui.eric.bow.algebras

import arrow.core.Either
import com.publicmethod.archer.Result
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.algebras.EricError

sealed class EricResult : Result {
    data class InitializeResult(
            val eric: Either<EricError, Eric>
    ) : EricResult()

    object ShowLoadingResult : EricResult()
    object EmailEricResult : EricResult()
    object DismissSnackBarResult : EricResult()
}