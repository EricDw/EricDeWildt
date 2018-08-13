package com.publicmethod.archer

import arrow.core.*
import arrow.data.State
import arrow.data.run
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

//region aliases
typealias FunctionWorker = SendChannel<FunctionWorkerMessage>

typealias OptionalSendChannel<T> = Option<SendChannel<T>>
//endregion aliases

//region Algebraic Data Types

sealed class FunctionWorkerMessage {
    data class StartOrRestartWork(val jobs: List<Pair<String, suspend () -> Job>>) : FunctionWorkerMessage()
    data class StopWork(val key: String, val cause: Throwable?) : FunctionWorkerMessage()
}

//endregion Algebraic Data Types

//region Interfaces

interface Command
interface Action
interface Result
interface StateData

interface Bow<
        A : Action,
        R : Result,
        C : Command,
        S : StateData> {
    fun commandChannel(): SendChannel<C>
    fun processorChannel(): SendChannel<A>
    fun reducerChannel(): SendChannel<R>
    fun stateChannel(): ReceiveChannel<S>
}

//endregion Interfaces

//region Functions

fun <A : Action,
        R : Result,
        C : Command,
        IS : StateData,
        PS : StateData,
        RS : StateData> bow(
        initialInterpreterState: Option<IS> = None,
        initialProcessorState: Option<PS> = None,
        initialReducerState: Option<RS> = None,
        interpret: (command: C, processor: OptionalSendChannel<A>) -> State<Option<IS>, Option<A>>,
        process: (action: A, reducer: OptionalSendChannel<R>) -> State<Option<PS>, Option<R>>,
        reduce: (result: R, stateChannel: OptionalSendChannel<RS>) -> State<Option<RS>, Option<RS>>,
        backgroundContext: CoroutineContext = CommonPool,
        parent: Job = Job()
) = object : Bow<A, R, C, RS> {

    private val stateChannel: Channel<RS> =
            Channel(Channel.UNLIMITED)

    private val reducer: SendChannel<R> =
            pipelineActor(
                    context = backgroundContext,
                    capacity = Channel.UNLIMITED,
                    parent = parent,
                    returnChannel = stateChannel.some(),
                    initialInternalState = initialReducerState,
                    handle = reduce)

    private val processor: SendChannel<A> =
            pipelineActor(
                    context = backgroundContext,
                    capacity = Channel.UNLIMITED,
                    parent = parent,
                    returnChannel = Some(reducer),
                    initialInternalState = initialProcessorState,
                    handle = process)

    private val interpreter: SendChannel<C> =
            pipelineActor(
                    context = backgroundContext,
                    capacity = Channel.UNLIMITED,
                    parent = parent,
                    returnChannel = Some(processor),
                    initialInternalState = initialInterpreterState,
                    handle = interpret)

    override fun commandChannel(): SendChannel<C> =
            interpreter

    override fun processorChannel(): SendChannel<A> =
            processor

    override fun reducerChannel(): SendChannel<R> =
            reducer

    override fun stateChannel(): ReceiveChannel<RS> =
            stateChannel
}


fun functionWorker(
        parentJob: Job = Job(),
        backgroundContext: CoroutineContext = CommonPool
) = actor<FunctionWorkerMessage>(
        parent = parentJob,
        context = backgroundContext) {

    val workMap: MutableMap<String, Pair<suspend () -> Job, Job>> = mutableMapOf()

    for (msg in channel) when (msg) {
        is FunctionWorkerMessage.StartOrRestartWork -> {
            for ((key, function) in msg.jobs) {
                workMap[key] = function to function()
            }
        }

        is FunctionWorkerMessage.StopWork -> {
            workMap[msg.key]?.second?.cancel()
        }
    }
}

suspend fun FunctionWorker.startOrRestartWork(
        jobs: List<Pair<String, suspend () -> Job>>
): Unit = send(FunctionWorkerMessage.StartOrRestartWork(jobs))


suspend fun FunctionWorker.stopWork(
        key: String, cause: Throwable?
): Unit = send(FunctionWorkerMessage.StopWork(key, cause))

fun <D, IS, R> pipelineActor(
        context: CoroutineContext = CommonPool,
        capacity: Int = 0,
        parent: Job = Job(),
        initialInternalState: Option<IS>,
        returnChannel: OptionalSendChannel<R>,
        handle: (dep: D, OptionalSendChannel<R>) -> State<Option<IS>, Option<R>>
): SendChannel<D> =
        actor(
                context = context,
                capacity = capacity,
                parent = parent
        ) {
            var internalState: Option<IS> = initialInternalState
            for (dep in channel) {
                with(handle(dep, returnChannel).run(internalState)) {
                    internalState = a
                    b.map { r ->
                        returnChannel.map { rc ->
                            launch(context) {
                                rc.send(r)
                            }
                        }
                    }
                }
            }
        }

//endregion Functions

//region extensions

fun <A> A.toId() = Id.just(this)

//endregion extensions
