package com.emad.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emad.data.local.dao.EmailDao
import com.emad.data.local.dao.UserDao
import com.emad.data.local.entity.EmailEntity
import com.emad.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, EmailEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // <--- Tell Room to use converters
abstract class AppDatabase : RoomDatabase() {

    abstract fun emailDao(): EmailDao
    abstract fun userDao(): UserDao
}