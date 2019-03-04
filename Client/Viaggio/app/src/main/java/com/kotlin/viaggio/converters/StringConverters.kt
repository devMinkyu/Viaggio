package com.kotlin.viaggio.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class StringConverters {
    @TypeConverter
    fun fromString(value: String): ArrayList<String> = Gson().fromJson(value, object : TypeToken<ArrayList<String>>() {}.type)
    @TypeConverter
    fun fromArrayList(list: ArrayList<String>) = Gson().toJson(list)!!
}