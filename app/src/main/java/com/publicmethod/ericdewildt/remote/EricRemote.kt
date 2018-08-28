package com.publicmethod.ericdewildt.remote

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.Try
import com.publicmethod.ericdewildt.remote.algebras.ApiError
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object EricRemote : Remote<ApiError, EricDTO> {
    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl("https://ericapi.azurewebsites.net/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val ericService: EricService
        get() = retrofit.create(EricService::class.java)

    override fun getEric(): Either<ApiError, EricDTO> =
        Try {
            ericService.ericCall().execute().body() ?: throw ApiError.NotFoundError
        }.fold({
            when (it) {
                is IOException -> Left(ApiError.NotFoundError)
                else -> Left(ApiError.NotFoundError)
            }
        }, { Right(it) })

}