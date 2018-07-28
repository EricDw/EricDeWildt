package com.publicmethod.ericdewildt.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SkillsDao {

    @Query("SELECT * FROM $skillsTableName")
    fun retrieveSkills(): List<CachedSkill>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSkill(cachedSkill: CachedSkill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSkills(cachedSkills: List<CachedSkill>)

}
