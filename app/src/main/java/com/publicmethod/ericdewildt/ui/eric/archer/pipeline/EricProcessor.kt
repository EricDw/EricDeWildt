package com.publicmethod.ericdewildt.ui.eric.archer.pipeline

import arrow.core.*
import arrow.data.Reader
import com.publicmethod.archer.*
import com.publicmethod.ericdewildt.threading.ContextProvider
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricAction
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricAction.*
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricResult
import com.publicmethod.ericdewildt.ui.eric.archer.algebras.EricResult.*
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

fun processEricAction(
    context: CoroutineContext = ContextProvider().backgroundContext()
): Reader<
        Tuple2<EricAction, ResultChannel<EricResult>>,
        Option<EricResult>
        > =
    Reader { (action, resultChannel) ->
        when (action) {
            is InitializeAction ->
                processInitializeAction(
                    action,
                    resultChannel,
                    context
                )

            is EmailEricAction -> {
                processEmailEricAction(
                    action,
                    resultChannel,
                    context
                )
            }

            is DismissSnackBarAction ->
                processDismissSnackBarKommand(
                    action,
                    resultChannel,
                    context
                )
        }.some().toId()

    }

fun processInitializeAction(
    action: InitializeAction,
    returnChannel: ResultChannel<EricResult>,
    context: CoroutineContext
): EricResult {
    launch(context) {
        returnChannel.send(
            LoadingResult
        )
    }
    return InitializeResult(action.getEricScope.ericRepository.getItem())
}

fun processEmailEricAction(
    action: EmailEricAction,
    returnChannel: ResultChannel<EricResult>,
    context: CoroutineContext
): EricResult {
    issueWork(action, returnChannel, context)
    return EmailEricResult
}

fun processDismissSnackBarKommand(
    action: EricAction.DismissSnackBarAction,
    resultChannel: ResultChannel<EricResult>,
    context: CoroutineContext
): EricResult {
    issueWork(action, resultChannel, context)
    return DismissSnackBarResult
}

fun issueWork(
    action: EricAction,
    resultChannel: ResultChannel<EricResult>,
    context: CoroutineContext
) {
    launch(context) {
        resultChannel.send(EricResult.IssueWorkResult(action, resultChannel))
    }
}


