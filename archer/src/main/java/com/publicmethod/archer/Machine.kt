package com.publicmethod.archer

import arrow.core.Option
import arrow.data.State
import arrow.data.run
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

interface SVCMachine<
        A : Archer.Action,
        R : Archer.Result,
        C : Archer.Command,
        S : Archer.State> {
    fun commandChannel(): SendChannel<C>
    fun processorChannel(): SendChannel<A>
    fun reducerChannel(): SendChannel<R>
    fun stateChannel(): ReceiveChannel<S>
}

fun <A : Archer.Action,
        R : Archer.Result,
        C : Archer.Command,
        IS : Archer.State,
        PS : Archer.State,
        RS : Archer.State> svcMachine(
        initialInterpreterState: IS,
        initialProcessorState: PS,
        initialReducerState: RS,
        interpret: (command: C) -> State<IS, Option<A>>,
        process: (action: A, reducer: SendChannel<R>) -> State<PS, Option<R>>,
        reduce: (result: R) -> State<RS, RS>,
        backgroundContext: CoroutineContext,
        parent: Job = Job()
) = object : SVCMachine<A, R, C, RS> {

    private val stateChannel: Channel<RS> =
            Channel(Channel.UNLIMITED)

    private val reducer: SendChannel<R> =
            actor(backgroundContext,
                    capacity = Channel.UNLIMITED,
                    parent = parent) {
                var internalState: RS = initialReducerState
                for (result in channel) {
                    with(reduce(result).run(internalState)) {
                        internalState = a
                        stateChannel.send(b)
                    }
                }
            }


    private val processor: SendChannel<A> =
            actor(backgroundContext,
                    capacity = Channel.UNLIMITED,
                    parent = parent) {
                var internalState: PS = initialProcessorState
                for (action in channel) {
                    with(process(action, reducer).run(internalState)) {
                        internalState = a
                        b.map {
                            launch(backgroundContext) {
                                reducer.send(it)
                            }
                        }
                    }
                }
            }

    private val interpreter: SendChannel<C> =
            actor(context = backgroundContext,
                    capacity = Channel.UNLIMITED,
                    parent = parent) {
                var internalState: IS = initialInterpreterState
                for (command in channel) {
                    with(interpret(command).run(internalState)) {
                        internalState = a
                        b.map {
                            launch(backgroundContext) {
                                processor.send(it)
                            }
                        }
                    }
                }
            }

    override fun commandChannel(): SendChannel<C> =
            interpreter

    override fun processorChannel(): SendChannel<A> =
            processor

    override fun reducerChannel(): SendChannel<R> =
            reducer

    override fun stateChannel(): ReceiveChannel<RS> =
            stateChannel
}