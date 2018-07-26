package com.publicmethod.ericdewildt.cache

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.publicmethod.ericdewildt.data.*
import java.util.*

@Entity(tableName = ericTableName)
data class CachedEric(
        @PrimaryKey(autoGenerate = false)
        var id: String = UUID.randomUUID().toString(),
        @ColumnInfo(name = com.publicmethod.ericdewildt.cache.firstName)
        var firstName: FullName = "",
        @ColumnInfo(name = com.publicmethod.ericdewildt.cache.lastName)
        var lastName: LastName = "",
        @ColumnInfo(name = com.publicmethod.ericdewildt.cache.fullName)
        var fullName: FullName = "",
        var description: Description = "",
        @ColumnInfo(name = com.publicmethod.ericdewildt.cache.yearsOfExperience)
        var yearsOfExperience: YearsOfExperience = 0,
        @Ignore
        val skills: Skills = listOf())

