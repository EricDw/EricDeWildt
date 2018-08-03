@file:Suppress("FunctionName", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.publicmethod.archer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Id
import arrow.data.run
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

object Archer {

//region Interfaces

    interface Kommand
    interface Action
    interface State
    interface Result
    interface Model

    interface Kommandable<C : Kommand> {
        fun issueKommand(kommand: C)
    }

    interface StateHandler<S : State> {
        fun handleState(state: S)
    }

//endregion Interfaces

    sealed class FletchingMessage {
        open class ActionMessage<A : Action>(val action: A) : FletchingMessage()
        open class ResultMessage<R : Result>(val result: R) : FletchingMessage()
        open class KommandMessage<K : Kommand>(val Kommand: K) : FletchingMessage()
        open class StateMessage<S : State>(val state: S) : FletchingMessage()
    }

    fun <M : Model, A : Action, R : Result, K : Kommand, S : State> arrowFletching(
            parentJob: Job,
            backgroundContext: CoroutineContext,
            uiContext: CoroutineContext,
            initialModel: M,
            interpreter: (parentJob: Job,
                          backgroundContext: CoroutineContext,
                          channel: SendChannel<FletchingMessage>)
            -> SendChannel<K>,
            processor: (parentJob: Job,
                        backgroundContext: CoroutineContext,
                        channel: SendChannel<FletchingMessage>)
            -> SendChannel<A>,
            reduceState: (result: R) -> arrow.data.State<M, S>,
            stateHandler: (parentJob: Job,
                           uiContext: CoroutineContext)
            -> SendChannel<S>
    ): SendChannel<FletchingMessage> = actor(
            parent = parentJob,
            context = backgroundContext,
            capacity = Channel.UNLIMITED) {

        val interpreterActor by lazy { interpreter(parentJob, backgroundContext, channel) }
        val processorActor by lazy { processor(parentJob, backgroundContext, channel) }
        val reducerActor by lazy { reducerActor(parentJob, backgroundContext, channel, initialModel, reduceState) }
        val stateHandlerActor by lazy { stateHandler(parentJob, uiContext) }

        for (msg in channel) {
            when (msg) {
                is FletchingMessage.ActionMessage<*> -> processorActor.send(msg.action as A)
                is FletchingMessage.ResultMessage<*> -> reducerActor.send(msg.result as R)
                is FletchingMessage.KommandMessage<*> -> interpreterActor.send(msg.Kommand as K)
                is FletchingMessage.StateMessage<*> -> stateHandlerActor.send(msg.state as S)
            }
        }
    }

    fun <M : Model, R : Result, S : State> reducerActor(
            parentJob: Job,
            backgroundContext: CoroutineContext,
            fletching: SendChannel<FletchingMessage>,
            initialModel: M,
            reduceState: (result: R) -> arrow.data.State<M, S>
    ): SendChannel<R> = actor(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob) {

        var currentModel: M = initialModel

        for (result in this.channel) {
            val reduction = reduceState(result).run(currentModel)
            currentModel = reduction.a
            fletching.send(FletchingMessage.StateMessage(reduction.b))
        }
    }

    abstract class Bow<
            M : Model,
            A : Action,
            R : Result,
            K : Kommand,
            S : State>(protected val backgroundContext: CoroutineContext,
                              protected val uiContext: CoroutineContext,
                              initialModel: M,
                              parentJob: Job = Job(),
                              interpreter: (parentJob: Job,
                                            backgroundContext: CoroutineContext,
                                            channel: SendChannel<FletchingMessage>)
                              -> SendChannel<K>,
                              processor: (parentJob: Job,
                                          backgroundContext: CoroutineContext,
                                          channel: SendChannel<FletchingMessage>)
                              -> SendChannel<A>,
                              reduceState: (result: R) -> arrow.data.State<M, S>
    ) : ViewModel(),
            Kommandable<K>,
            StateHandler<S> {

        protected open val mutableState: MutableLiveData<S> = MutableLiveData()

        open val state: LiveData<S>
            get() = mutableState

        protected open fun stateHandler(parentJob: Job,
                                        uiContext: CoroutineContext)
                : SendChannel<S> = actor(parent = parentJob,
                context = uiContext,
                capacity = Channel.UNLIMITED) {
            for (state in channel) {
                handleState(state)
            }
        }

        protected val fletching = arrowFletching(
                parentJob,
                backgroundContext,
                uiContext,
                initialModel,
                interpreter,
                processor,
                reduceState,
                ::stateHandler)

        override fun issueKommand(kommand: K) {
            launch(backgroundContext) {
                fletching.send(FletchingMessage.KommandMessage(kommand))
            }
        }

        override fun handleState(state: S) {
            launch(uiContext) {
                mutableState.value = state
            }
        }
    }
}

fun <A> A.toId() = Id.just(this)
