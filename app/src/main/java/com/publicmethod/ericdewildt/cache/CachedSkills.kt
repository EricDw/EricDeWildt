package com.publicmethod.ericdewildt.cache

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = skillsTableName)
data class CachedSkill(
        @PrimaryKey(autoGenerate = false)
        val name: String
)
