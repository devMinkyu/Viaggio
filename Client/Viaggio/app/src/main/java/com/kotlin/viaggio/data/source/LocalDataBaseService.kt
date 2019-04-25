package com.kotlin.viaggio.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kotlin.viaggio.converters.AreaConverters
import com.kotlin.viaggio.converters.DateTypeConverters
import com.kotlin.viaggio.converters.StringConverters
import com.kotlin.viaggio.converters.StringOfListConverters
import com.kotlin.viaggio.data.`object`.*


@Suppress("unused")
@Database(
    entities = [User::class, Travel::class, TravelOfDay::class, TravelCard::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringConverters::class, DateTypeConverters::class, StringOfListConverters::class, AreaConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun travelDao(): TravelDao
}