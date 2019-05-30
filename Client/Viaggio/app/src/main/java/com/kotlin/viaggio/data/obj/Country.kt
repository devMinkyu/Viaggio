package com.kotlin.viaggio.data.obj

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "countries")
data class Country(
    @PrimaryKey(autoGenerate = true) var id:Long = 0,
    var continent: String = "",
    var country: String,
    var url: String = "",
    @Suppress("ArrayInDataClass") var area: ArrayList<String>,
    var kind: Int = 0
)
// 0 해외
// 1 국내

data class Area(
    var continent: String = "",
    var country: String ="",
    var city:String ="",
    var selected: ObservableBoolean = ObservableBoolean(false)
)

data class CountryBody(
    var contries:List<Country>
)
data class DomesticsBody(
    var domestics:List<Country>
)