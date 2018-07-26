package com.publicmethod.ericdewildt.cache

import arrow.core.*
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.Repository
import com.publicmethod.ericdewildt.data.algebras.EricError
import com.publicmethod.ericdewildt.data.mapToCachedEric
import com.publicmethod.ericdewildt.data.mapToEric
import com.publicmethod.ericdewildt.remote.EricDTO
import com.publicmethod.ericdewildt.remote.Remote
import com.publicmethod.ericdewildt.remote.algebras.ApiError

class EricRepository(private val ericRemote: Remote<ApiError, EricDTO>,
                     private val ericCache: Cache<CachedEric>) : Repository<EricError, Eric> {

    override fun getItem(): Either<EricError, Eric> =
            ericCache.getItem()
                    .fold({
                        retrieveEricFromRemote()
                                .fold({ apiError ->
                                    handleApiError(apiError)
                                }, { ericDTO ->
                                    ericCache.saveItem(ericDTO.mapToCachedEric())
                                    Right(ericDTO.mapToEric())
                                })
                    }, { cachedEric ->
                        Right(cachedEric.mapToEric())
                    })

    private fun retrieveEricFromRemote(): Either<ApiError, EricDTO> =
            ericRemote.getEric().fold({ apiError ->
                Left(apiError)
            }, { ericDTO ->
                Right(ericDTO)
            })

    private fun handleApiError(error: ApiError): Either<EricError, Eric> =
            when (error) {
                is ApiError.NotFoundError -> Left(EricError.EricNotFoundError)
            }

}