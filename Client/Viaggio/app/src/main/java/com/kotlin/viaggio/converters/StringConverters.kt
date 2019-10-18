package com.kotlin.viaggio.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.data.obj.Area


class StringConverters {
    @TypeConverter
    fun fromString(value: String): ArrayList<String> {
        return try {
            Gson().fromJson(value, object : TypeToken<ArrayList<String>>() {}.type)
        }catch (e:IllegalStateException) {
            arrayListOf()
        }
    }
    @TypeConverter
    fun fromArrayList(list: ArrayList<String>) = Gson().toJson(list)!!
}

class StringOfListConverters {
    @TypeConverter
    fun fromString(value: String?): MutableList<String> {
        return try {
            Gson().fromJson(value, object : TypeToken<MutableList<String>>() {}.type)
        }catch (e:IllegalStateException) {
            mutableListOf()
        }
    }
    @TypeConverter
    fun fromArrayList(list: MutableList<String>) = Gson().toJson(list)!!
}

class AreaConverters {
    @TypeConverter
    fun fromString(value: String): MutableList<Area> {
        return try {
            Gson().fromJson(value, object : TypeToken<MutableList<Area>>() {}.type)
        }catch (e:IllegalStateException) {
            mutableListOf()
        }
    }
    @TypeConverter
    fun fromArrayList(list: MutableList<Area>) = Gson().toJson(list)!!
}