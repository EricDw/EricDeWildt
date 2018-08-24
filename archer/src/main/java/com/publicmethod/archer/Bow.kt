package com.publicmethod.archer

import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.data.State
import arrow.data.runA
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlin.coroutines.experimental.CoroutineContext

//region aliases

typealias FunctionWorker = SendChannel<FunctionWork>

typealias JobName = String

//endregion aliases

//region Data Classes

data class Work(val name: JobName, val jobFunction: suspend () -> Job)

//endregion Data Classes

//region Algebraic Data Types

sealed class FunctionWork {
    data class StartOrRestartWork(val jobs: List<Work>) : FunctionWork()
    data class StopWork(val name: JobName, val cause: Option<Throwable>) : FunctionWork()
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
    fun close()
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
    interpret: (command: C, processor: SendChannel<A>) ->
    State<Option<IS>, Option<IS>>,
    process: (action: A, reducer: SendChannel<R>) ->
    State<Option<PS>, Option<PS>>,
    reduce: (result: R, stateChannel: SendChannel<RS>) ->
    State<Option<RS>, Option<RS>>,
    backgroundContext: CoroutineContext = CommonPool,
    parent: Job = Job()
) = object : Bow<A, R, C, RS> {

    private val stateChannel: Channel<RS> =
        Channel(Channel.UNLIMITED)

    private val reducer: SendChannel<R> =
        activeActor(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parent,
            returnChannel = stateChannel,
            initialInternalState = initialReducerState,
            reduceState = reduce
        )

    private val processor: SendChannel<A> =
        activeActor(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parent,
            returnChannel = reducer,
            initialInternalState = initialProcessorState,
            reduceState = process
        )

    private val interpreter: SendChannel<C> =
        activeActor(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parent,
            returnChannel = processor,
            initialInternalState = initialInterpreterState,
            reduceState = interpret
        )

    override fun commandChannel(): SendChannel<C> =
        interpreter

    override fun processorChannel(): SendChannel<A> =
        processor

    override fun reducerChannel(): SendChannel<R> =
        reducer

    override fun stateChannel(): ReceiveChannel<RS> =
        stateChannel

    override fun close() {
        stateChannel.close()
        reducer.close()
        processor.close()
        interpreter.close()
    }
}

fun <D, IS, R> activeActor(
    context: CoroutineContext = CommonPool,
    capacity: Int = 0,
    parent: Job = Job(),
    initialInternalState: Option<IS>,
    returnChannel: SendChannel<R>,
    reduceState: (dep: D, SendChannel<R>) -> State<Option<IS>, Option<IS>>
): SendChannel<D> =
    actor(
        context = context,
        capacity = capacity,
        parent = parent
    ) {
        var internalState: Option<IS> = initialInternalState
        for (dep in channel) {
            internalState = reduceState(dep, returnChannel).runA(internalState)
        }
    }

fun functionWorker(
    parentJob: Job = Job(),
    backgroundContext: CoroutineContext = CommonPool
) = actor<FunctionWork>(
    parent = parentJob,
    context = backgroundContext
) {

    val workMap: MutableMap<String, Pair<suspend () -> Job, Job>> = mutableMapOf()

    for (msg in channel) when (msg) {
        is FunctionWork.StartOrRestartWork -> {
            for ((key, function) in msg.jobs) {
                workMap[key] = function to function()
            }
        }

        is FunctionWork.StopWork -> {
            workMap[msg.name]?.second?.cancel(
                msg.cause.orNull()
            )
        }
    }
}

suspend fun FunctionWorker.startOrRestartWork(
    jobs: List<Work>
): Unit = send(FunctionWork.StartOrRestartWork(jobs))

suspend fun FunctionWorker.stopWork(
    key: String, cause: Option<Throwable>
): Unit = send(FunctionWork.StopWork(key, cause))

//endregion Functions

//region extension Functions

fun <A> A.toId() = Id.just(this)

infix fun JobName.to(that: suspend () -> Job): Work = Work(this, that)


//endregion extension Functions
