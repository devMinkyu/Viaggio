package com.kotlin.viaggio.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.`object`.User

@Suppress("unused")
@Database(entities = [User::class, Travel::class, TravelOfDay::class, TravelCard::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun travelDao(): TravelDao
}