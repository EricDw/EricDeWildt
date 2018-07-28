@file:Suppress("MemberVisibilityCanBePrivate")

package com.publicmethod.archer

import androidx.lifecycle.ViewModel
import com.publicmethod.archer.Archer.Command
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

abstract class LightBow<C : Command,
        A : Archer.Action,
        R : Archer.Result,
        S : Archer.State,
        M : Archer.Model>(
        private val uiContext: CoroutineContext,
        private val backgroundContext: CoroutineContext) : ViewModel(),
        Archer.Commandable<C>, Archer.StateHandler<S> {

    protected abstract val interpreter: Archer.CommandInterpreter<C, A>
    protected abstract val processor: Archer.ActionProcessor<A, R>
    protected abstract val reducer: Archer.StateReducer<R, M, S>

    protected val parentJob = Job()

    private val interpreterFletching = actor<C>(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob
    ) {
        for (command in channel) {
            interpreterActor.send(command)
        }
    }

    private val processorFletching = actor<A>(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob
    ) {
        for (action in channel) {
            processorActor.send(action)
        }
    }

    private val reducerFletching = actor<R>(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob
    ) {
        for (result in channel) {
            reducerActor.send(result)
        }
    }

    private val stateFletching = actor<S>(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob
    ) {
        for (state in channel) {
            launch(uiContext) {
                handleState(state)
            }
        }
    }

    private val interpreterActor = actor<C>(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob
    ) {
        for (command in channel) {
            processorFletching.send(interpreter.interpret(command))
        }
    }

    private val processorActor = actor<A>(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob
    ) {
        for (action in channel) {
            reducerFletching.send(processor.process(action))
        }
    }

    private val reducerActor = actor<R>(
            context = backgroundContext,
            capacity = Channel.UNLIMITED,
            parent = parentJob
    ) {
        for (result in channel) {
            stateFletching.send(reducer.reduce(result))
        }
    }

    override fun issueCommand(command: C) {
        launch(backgroundContext) {
            interpreterFletching.send(command)
        }
    }
}

