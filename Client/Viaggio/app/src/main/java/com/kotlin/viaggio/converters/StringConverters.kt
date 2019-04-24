package com.kotlin.viaggio.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.data.`object`.Area


class StringConverters {
    @TypeConverter
    fun fromString(value: String): ArrayList<String> = Gson().fromJson(value, object : TypeToken<ArrayList<String>>() {}.type)
    @TypeConverter
    fun fromArrayList(list: ArrayList<String>) = Gson().toJson(list)!!
}

class StringOfListConverters {
    @TypeConverter
    fun fromString(value: String): MutableList<String> = Gson().fromJson(value, object : TypeToken<MutableList<String>>() {}.type)
    @TypeConverter
    fun fromArrayList(list: MutableList<String>) = Gson().toJson(list)!!
}

class AreaConverters {
    @TypeConverter
    fun fromString(value: String): MutableList<Area> = Gson().fromJson(value, object : TypeToken<MutableList<Area>>() {}.type)
    @TypeConverter
    fun fromArrayList(list: MutableList<Area>) = Gson().toJson(list)!!
}