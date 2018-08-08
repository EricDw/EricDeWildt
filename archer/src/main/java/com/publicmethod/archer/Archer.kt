@file:Suppress("FunctionName", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.publicmethod.archer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Id
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext


object Archer {

    sealed class FunctionWorkerMessage {
        data class StartWork(val f: suspend () -> Unit) : FunctionWorkerMessage()
        object StopWork : FunctionWorkerMessage()
    }

//region Interfaces

    interface Kommand
    interface Action
    interface State
    interface Result
    interface Model

    interface Interpreter<K : Kommand, A : Action> {
        suspend fun interpret(kommand: K, actionChannel: SendChannel<A>)
    }

    interface Processor<A : Action, R : Result> {
        suspend fun process(action: A, resultChannel: SendChannel<R>)
    }

    interface Reducer<R : Result, S : State> {
        suspend fun reduce(result: R, stateChannel: SendChannel<S>)
    }

//endregion Interfaces

    fun <K : Kommand, A : Action> interpreterChannel(
            interpreter: Interpreter<K, A>,
            actionChannel: SendChannel<A>,
            parent: Job,
            context: CoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT
    ): SendChannel<K> = actor(
            parent = parent,
            context = context,
            capacity = Channel.UNLIMITED,
            start = start) {
        for (kommand in channel) {
            interpreter.interpret(kommand, actionChannel)
        }
    }

    fun <A : Action, R : Result> processorChannel(
            processor: Processor<A, R>,
            resultChannel: SendChannel<R>,
            parent: Job,
            context: CoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT
    ): SendChannel<A> = actor(
            parent = parent,
            context = context,
            capacity = Channel.UNLIMITED,
            start = start) {
        for (action in channel) {
            processor.process(action, resultChannel)
        }
    }

    fun <R : Result, S : State> reducerChannel(
            reducer: Reducer<R, S>,
            stateChannel: SendChannel<S>,
            parent: Job,
            context: CoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT
    ): SendChannel<R> = actor(
            parent = parent,
            context = context,
            capacity = Channel.UNLIMITED,
            start = start) {
        for (result in channel) {
            reducer.reduce(result, stateChannel)
        }
    }

    abstract class Bow<
            A : Action,
            R : Result,
            K : Kommand,
            S : State>(
            parent: Job = Job(),
            backgroundContext: () -> CoroutineContext,
            reducer: () -> Reducer<R, S>,
            processor: () -> Processor<A, R>,
            interpreter: () -> Interpreter<K, A>
    ) : ViewModel() {

        private val background = backgroundContext()

        protected open val mutableState: MutableLiveData<S> = MutableLiveData()

        open val state: LiveData<S>
            get() = mutableState

        protected open val stateChannel: SendChannel<S> = actor(
                context = background,
                parent = parent,
                capacity = Channel.UNLIMITED
        ) {
            for (state in channel) {
                mutableState.postValue(state)
            }
        }

        protected open val reducerChannel: SendChannel<R> by lazy {
            reducerChannel(
                    reducer(),
                    stateChannel,
                    parent,
                    background)
        }

        protected open val processorChannel: SendChannel<A> by lazy {
            processorChannel(
                    processor(),
                    reducerChannel,
                    parent,
                    background)
        }

        protected open val interpreterChannel: SendChannel<K> by lazy {
            interpreterChannel(
                    interpreter(),
                    processorChannel,
                    parent,
                    background)
        }

        open fun issueKommand(kommand: K) {
            launch(context = background) {
                interpreterChannel.send(kommand)
            }
        }

    }

    fun functionWorker(
            parentJob: Job = Job(),
            backgroundContext: CoroutineContext = CommonPool
    ) = actor<FunctionWorkerMessage>(
            parent = parentJob,
            context = backgroundContext) {

        var job: Job? = null

        for (msg in channel) when (msg) {
            is FunctionWorkerMessage.StartWork -> {
                job?.cancel()
                job = launch {
                    msg.f()
                }
            }
            is FunctionWorkerMessage.StopWork -> job?.cancel()
        }
    }


}

fun <A> A.toId() = Id.just(this)