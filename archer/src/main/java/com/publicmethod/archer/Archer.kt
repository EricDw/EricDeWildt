package com.publicmethod.archer

import arrow.core.*
import arrow.data.Reader
import arrow.data.State
import arrow.data.runA
import arrow.data.runId
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlin.coroutines.experimental.CoroutineContext

//region aliases

typealias FunctionWorker = SendChannel<FunctionWork>
typealias CommandChannel<T> = SendChannel<T>
typealias StateChannel<T> = ReceiveChannel<T>
typealias ActionChannel<T> = SendChannel<T>
typealias ResultChannel<T> = SendChannel<T>

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

interface Archer<C : Command, S : StateData> {
    fun commandChannel(): CommandChannel<C>
    fun stateChannel(): StateChannel<S>
    fun close()
}

//endregion Interfaces

//region Functions

fun <D, R> runner(
    context: CoroutineContext = CommonPool,
    capacity: Int = 0,
    parent: Job = Job(),
    returnChannel: SendChannel<R>,
    reader: Reader<Tuple2<D, SendChannel<R>>, Option<R>>
): SendChannel<D> =
    actor(
        context = context,
        capacity = capacity,
        parent = parent
    ) {
        for (a in channel) {
            reader.runId(a toT returnChannel).also { option ->
                option.fold({}, {
                    returnChannel.send(it)
                })
            }
        }
    }

/**
 * Essentially just an [actor]  housing
 * an entire MVI framework but exposing only the
 * [CommandChannel] of [C] and the
 * [StateChannel] of [S] parts.
 *
 *
 * @param [interpret] A [Reader] of [Tuple2] of ([C] and
 * [SendChannel] of [A]), this is where the mapping
 * of [Command] to [Action] will take place.
 *
 * @param [process] A [Reader] of [Tuple2] of ([A] and
 * [SendChannel] of [R]), this is where the mapping
 * of [Action] to [Result] will take place.
 *
 * @param [reduce] A [Reader] of [Tuple2] of ([R] and
 * [SendChannel] of [S]), this is where the mapping
 * of [Result] to [StateData] will take place.
 *
 *
 * @return An anonymous object implementing the [Archer] interface.
 */
fun <A : Action,
        R : Result,
        C : Command,
        S : StateData> archer(
    context: CoroutineContext = CommonPool,
    parentJob: Job = Job(),
    initialState: Option<S> = None,
    interpret: Reader<Tuple2<C, ActionChannel<A>>, Option<A>>,
    process: Reader<Tuple2<A, ResultChannel<R>>, Option<R>>,
    reduce: Reader<Tuple2<R, SendChannel<S>>, State<Option<S>, Option<S>>>
): Archer<C, S> = object : Archer<C, S> {

    private var state = initialState

    val stateChannel = Channel<S>(Channel.UNLIMITED)

    val reducer: SendChannel<R> =
        runner(
            context,
            Channel.UNLIMITED,
            parentJob,
            stateChannel,
            reader = reduce.map(Id.functor()) {
                state = it.runA(state)
                state
            }
        )

    val processor: SendChannel<A> =
        runner(
            context,
            Channel.UNLIMITED,
            parentJob,
            reducer,
            process
        )

    val interpreter: SendChannel<C> =
        runner(
            context,
            Channel.UNLIMITED,
            parentJob,
            processor,
            interpret
        )

    override fun commandChannel(): SendChannel<C> =
        interpreter

    override fun stateChannel(): ReceiveChannel<S> =
        stateChannel

    override fun close() {
        stateChannel.close()
        reducer.close()
        processor.close()
        interpreter.close()
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
