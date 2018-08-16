package com.publicmethod.ericdewildt.scopes

import android.content.Context
import arrow.core.Option
import com.publicmethod.ericdewildt.cache.EricCache
import com.publicmethod.ericdewildt.cache.EricDataBase.Companion.getInstance
import com.publicmethod.ericdewildt.cache.EricRepository
import com.publicmethod.ericdewildt.data.Eric
import com.publicmethod.ericdewildt.data.Repository
import com.publicmethod.ericdewildt.data.algebras.EricError
import com.publicmethod.ericdewildt.remote.EricRemote


data class GetEricScope(val ericRepository: Repository<EricError, Eric>)

fun getEricScope(context: Context): Option<GetEricScope> =
    getInstance(context).map { ericDataBase ->
        GetEricScope(
            EricRepository(
                EricRemote,
                EricCache(ericDataBase.ericDao(), ericDataBase.skillsDao())
            )
        )
    }
