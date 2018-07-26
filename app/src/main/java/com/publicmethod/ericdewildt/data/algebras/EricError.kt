package com.publicmethod.ericdewildt.data.algebras

sealed class EricError : Throwable() {
    object EricNotFoundError : EricError()
}