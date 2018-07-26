package com.publicmethod.ericdewildt.data

import arrow.core.Either
import arrow.core.Right
import com.publicmethod.ericdewildt.data.algebras.EricError
import com.publicmethod.ericdewildt.remote.algebras.ApiError

class TestRightEricRepository : Repository<EricError, Eric> {
    override fun getItem(): Either<EricError, Eric> = Right(Eric("Test Eric",
            "Test De Wildt",
            "Test Eric De Wildt",
            "Test Description",
            5,
            listOf("Android", "Arrow")))
}