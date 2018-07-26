package com.publicmethod.ericdewildt.data

import arrow.core.Either

interface Repository<E : Throwable, D> {
    fun getItem() : Either<E, D>
}