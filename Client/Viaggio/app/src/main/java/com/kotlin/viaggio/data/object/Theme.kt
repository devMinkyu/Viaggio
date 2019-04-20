package com.kotlin.viaggio.data.`object`

import androidx.databinding.ObservableBoolean

data class Theme(
    var id: Long = 0,
    var theme: String = "",
    var authority:Boolean = false
)

data class ThemeData(
    var theme:Theme,
    var select:ObservableBoolean = ObservableBoolean(false)
)