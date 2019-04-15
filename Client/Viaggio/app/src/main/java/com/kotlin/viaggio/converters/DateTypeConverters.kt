package com.kotlin.viaggio.converters

import androidx.room.TypeConverter
import java.util.*


class DateTypeConverters {
    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }
    @TypeConverter
    fun toLong(value: Date?): Long? {
        return (value?.time)
    }
}