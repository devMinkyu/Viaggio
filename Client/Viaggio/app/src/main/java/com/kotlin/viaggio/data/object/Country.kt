package com.kotlin.viaggio.data.`object`

data class Country(var continent: String, var areas: ArrayList<Area>)

data class Area(var area:String, var country: ArrayList<String>)


