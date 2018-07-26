package com.publicmethod.ericdewildt.cache

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface SkillsDao {

    @Query("SELECT * FROM $skillsTableName")
    fun retrieveSkills(): List<CachedSkill>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSkill(cachedSkill: CachedSkill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSkills(cachedSkills: List<CachedSkill>)

}
