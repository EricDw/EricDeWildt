@file:Suppress("FunctionName", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.publicmethod.archer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Id
import arrow.data.getOption
import com.publicmethod.archer.Archer.FunctionWorkerMessage.AddWork
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext


typealias FunctionWorker = SendChannel<Archer.FunctionWorkerMessage>

object Archer {

    sealed class FunctionWorkerMessage {
        data class StartOrRestartWork(val key: String) : FunctionWorkerMessage()
        data class AddWork(val key: String, val work: suspend () -> Unit) : FunctionWorkerMessage()
        data class StopWork(val key: String) : FunctionWorkerMessage()
    }

//region Interfaces

    interface Command
    interface Action
    interface Result
    interface State

    interface ViewController<C: Command, S: State> {
        val commands: ReceiveChannel<C>
        fun render(state: S)
    }

    interface Interpreter<C : Command, A : Action> {
        suspend fun interpret(command: C, actionChannel: SendChannel<A>)
    }

    interface Processor<A : Action, R : Result> {
        suspend fun process(action: A, resultChannel: SendChannel<R>)
    }

    interface Reducer<R : Result, S : State> {
        suspend fun reduce(result: R, stateChannel: SendChannel<S>)
    }

    interface StateMachine<C: Command, S: State> {
        fun handleCommands(commands: ReceiveChannel<C>)
        val state: LiveData<S>
    }

//endregion Interfaces

    fun <K : Command, A : Action> interpreterChannel(
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
            C : Command,
            S : State>(
            parent: Job = Job(),
            backgroundContext: () -> CoroutineContext,
            reducer: (CoroutineContext) -> Reducer<R, S>,
            processor: (CoroutineContext) -> Processor<A, R>,
            interpreter: (CoroutineContext) -> Interpreter<C, A>
    ) : ViewModel(), StateMachine<C, S> {

        private val background = backgroundContext()

        protected open val mutableState: MutableLiveData<S> = MutableLiveData()

        override val state: LiveData<S>
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
                    reducer(background),
                    stateChannel,
                    parent,
                    background)
        }

        protected open val processorChannel: SendChannel<A> by lazy {
            processorChannel(
                    processor(background),
                    reducerChannel,
                    parent,
                    background)
        }

        protected open val interpreterChannel: SendChannel<C> by lazy {
            interpreterChannel(
                    interpreter(background),
                    processorChannel,
                    parent,
                    background)
        }

        override fun handleCommands(commands: ReceiveChannel<C>) {
            launch(background) {
                for (command in commands){
                    interpreterChannel.send(command)
                }
            }
        }
    }

    fun functionWorker(
            parentJob: Job = Job(),
            backgroundContext: CoroutineContext = CommonPool
    ) = actor<FunctionWorkerMessage>(
            parent = parentJob,
            context = backgroundContext) {

        val work: MutableMap<String, suspend () -> Unit> = mutableMapOf()
        val jobs: MutableMap<String, Job> = mutableMapOf()

        for (msg in channel) when (msg) {
            is FunctionWorkerMessage.StartOrRestartWork -> {
                work.getOption(msg.key).fold({}, { function ->
                    jobs[msg.key]?.cancel()
                    jobs[msg.key] = launch(backgroundContext) {
                        function()
                    }
                })
            }
            is AddWork -> work[msg.key] = msg.work

            is FunctionWorkerMessage.StopWork -> {
                with(msg) {
                    jobs.getOption(key).fold({}, {
                        it.cancel()
                        jobs.remove(key)
                    })
                }
            }
        }
    }

    suspend fun FunctionWorker.startOrRestartWork(
            key: String
    ): Unit = send(FunctionWorkerMessage.StartOrRestartWork(key))


    suspend fun FunctionWorker.stopWork(
            key: String
    ): Unit = send(FunctionWorkerMessage.StopWork(key))


    suspend fun FunctionWorker.addWork(
            key: String,
            work: suspend () -> Unit
    ): Unit = send(AddWork(key, work))


}

fun <A> A.toId() = Id.just(this)
