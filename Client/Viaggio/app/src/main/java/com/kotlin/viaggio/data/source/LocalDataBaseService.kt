package com.kotlin.viaggio.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kotlin.viaggio.converters.AreaConverters
import com.kotlin.viaggio.converters.DateTypeConverters
import com.kotlin.viaggio.converters.StringConverters
import com.kotlin.viaggio.converters.StringOfListConverters
import com.kotlin.viaggio.data.obj.*


@Suppress("unused")
@Database(
    entities = [User::class, Travel::class, TravelCard::class, Theme::class, Country::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(StringConverters::class, DateTypeConverters::class, StringOfListConverters::class, AreaConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun travelDao(): TravelDao
    abstract fun themeDao(): ThemeDao
    abstract fun countryDao(): CountryDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE countries ADD COLUMN kind Integer Not NULL Default 0 ")
    }
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE travelCards ADD COLUMN newImageNames TEXT Not NULL Default '' ")
        database.execSQL("ALTER TABLE travelCards ADD COLUMN time TEXT")
    }
}