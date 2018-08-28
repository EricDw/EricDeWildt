package com.publicmethod.ericdewildt.ui.eric.bow.algebras

import arrow.core.Either
import com.publicmethod.archer.Result
import com.publicmethod.archer.ResultChannel
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.algebras.EricError

sealed class EricResult : Result {
    data class InitializeResult(
        val eric: Either<EricError, Eric>
    ) : EricResult()

    data class IssueWorkResult(
        val ericAction: EricAction,
        val resultChannel: ResultChannel<EricResult>
    ) : EricResult()

    object LoadingResult : EricResult()
    object EmailEricResult : EricResult()
    object DismissSnackBarResult : EricResult()
}