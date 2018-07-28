package com.publicmethod.ericdewildt.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EricDao {

    @Query("SELECT * FROM $ericTableName LIMIT 1")
    fun retrieveEric(): CachedEric?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEric(cachedEric: CachedEric)

}