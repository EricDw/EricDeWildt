package com.publicmethod.ericdewildt.cache

import arrow.core.Option

interface Cache<T> {
    fun getItem(): Option<T>
    fun isCached(): Boolean
    fun isStale(): Boolean
    fun saveItem(item: T)
}
