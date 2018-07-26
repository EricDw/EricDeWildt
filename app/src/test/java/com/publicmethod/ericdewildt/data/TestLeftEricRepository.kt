package com.publicmethod.ericdewildt.data

import arrow.core.Either
import arrow.core.Left
import com.publicmethod.ericdewildt.data.algebras.EricError

class TestLeftEricRepository : Repository<EricError, Eric> {
    override fun getItem(): Either<EricError, Eric> = Left(EricError.EricNotFoundError)
}