package com.publicmethod.ericdewildt.remote.algebras

sealed class ApiError : Throwable() {
    object NotFoundError : ApiError()
}