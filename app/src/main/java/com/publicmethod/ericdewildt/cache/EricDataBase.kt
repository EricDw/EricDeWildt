package com.publicmethod.ericdewildt.cache

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import arrow.core.None
import arrow.core.Option
import arrow.core.some

@Database(entities = [CachedEric::class, CachedSkill::class], version = 1)
abstract class EricDataBase : RoomDatabase() {
    abstract fun ericDao(): EricDao
    abstract fun skillsDao(): SkillsDao

    companion object {
        private var INSTANCE: Option<EricDataBase> = None

        fun getInstance(context: Context): Option<EricDataBase> {
            if (INSTANCE === None) {
                synchronized(EricDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            EricDataBase::class.java, ericDatabaseName)
                            .build().some()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = None
        }
    }
}