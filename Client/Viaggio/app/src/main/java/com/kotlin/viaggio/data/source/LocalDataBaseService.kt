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
@TypeConverters(
    StringConverters::class,
    DateTypeConverters::class,
    StringOfListConverters::class,
    AreaConverters::class
)
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
        database.execSQL("CREATE TABLE travelCards_new (localId Integer NOT NULL, serverId Integer NOT NULL, userExist INTEGER NOT NULL, travelLocalId INTEGER NOT NULL, travelServerId INTEGER NOT NULL, travelOfDay INTEGER NOT NULL, country Text not null, theme Text not null,imageNames Text not null,imageUrl Text not null,newImageNames Text not null, content TEXT NOT NULL, date INTEGER NOT NULL,time INTEGER NOT NULL,isDelete INTEGER NOT NULL, PRIMARY KEY(localId))")
        database.execSQL("INSERT INTO travelCards_new (localId, serverId, userExist, travelLocalId, travelServerId, travelOfDay, country, theme, imageNames, imageUrl, newImageNames, content, date, time, isDelete) SELECT localId, serverId, userExist, travelLocalId, travelServerId, travelOfDay, country, theme, imageNames, imageUrl, imageUrl, content, date, date, isDelete FROM questions")
        database.execSQL("DROP TABLE travelCards")
        database.execSQL("ALTER TABLE travelCards_new RENAME TO travelCards")
    }
}