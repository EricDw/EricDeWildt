package com.publicmethod.ericdewildt.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = skillsTableName)
data class CachedSkill(
        @PrimaryKey(autoGenerate = false)
        val name: String
)
