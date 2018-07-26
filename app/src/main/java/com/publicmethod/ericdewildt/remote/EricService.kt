package com.publicmethod.ericdewildt.remote

import retrofit2.Call
import retrofit2.http.GET

interface EricService {
    @GET("eric")
    fun ericCall(): Call<EricDTO>
}