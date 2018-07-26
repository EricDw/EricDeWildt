package com.publicmethod.ericdewildt.cache

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface EricDao {

    @Query("SELECT * FROM $ericTableName LIMIT 1")
    fun retrieveEric(): CachedEric?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEric(cachedEric: CachedEric)

}