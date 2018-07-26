package com.publicmethod.ericdewildt.remote

import arrow.core.Either

interface Remote<E, D> {
    fun getEric(): Either<E, D>
}
