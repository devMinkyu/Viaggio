package com.kotlin.viaggio.data.`object`

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "themes")
data class Theme(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var theme: String = "",
    var authority:Boolean = false
)

data class ThemeData(
    var id: Long = 0,
    var theme: String = "",
    var authority:Boolean = false,
    var select:ObservableBoolean = ObservableBoolean(false)
)