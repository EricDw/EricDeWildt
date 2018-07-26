@file:Suppress("FunctionName", "MemberVisibilityCanBePrivate")

package com.publicmethod.archer

import arrow.core.Id
import arrow.data.Reader
import arrow.data.run
import arrow.data.runId

object Archer {

//region Interfaces

    interface Command
    interface Action
    interface State
    interface Result
    interface Model

    interface Commandable<C : Command> {
        fun issueCommand(command: C)
    }

    interface StateHandler<S : State> {
        fun handleState(state: S)
    }

    abstract class ModelViewCommander<C : Command, A : Action, R : Result, S : State, M : Model>
        : Commandable<C>, StateHandler<S> {
       protected abstract val interpreter: CommandInterpreter<C, A>
        protected abstract val processor: ActionProcessor<A, R>
        protected abstract val reducer: StateReducer<R, M, S>
    }

//endregion Interfaces

//region Abstract Classes

    abstract class CommandInterpreter<C : Command, A : Action> {

        fun interpreterReader(): Reader<C, A> =
                Reader { command -> interpretCommand(command).toId() }

        fun interpret(command: C): A = interpreterReader().runId(command)

        protected abstract fun interpretCommand(command: C): A

    }

    abstract class ActionProcessor<A : Action, R : Result> {

        fun processorReader(): Reader<A, R> =
                Reader { action -> processAction(action).toId() }

        fun process(action: A): R = processorReader().runId(action)

        protected abstract fun processAction(action: A): R

    }

    abstract class StateReducer<R : Result,
            M : Model,
            S : State>(private var _oldModel: M) {

        val oldModel: M
            get() = _oldModel

        fun reduce(result: R): S {
            val reduction = reduceState(result).run(_oldModel)
            _oldModel = reduction.a
            return reduction.b
        }
        protected abstract fun reduceState(result: R): arrow.data.State<M, S>

    }

//endregion Abstract Classes

}

fun <A> A.toId() = Id.just(this)
