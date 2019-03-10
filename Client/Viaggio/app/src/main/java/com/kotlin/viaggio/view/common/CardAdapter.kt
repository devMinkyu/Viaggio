package com.kotlin.viaggio.view.common

import androidx.cardview.widget.CardView



interface CardAdapter{
    companion object {
        const val MAX_ELEVATION_FACTOR = 8
    }
    fun getBaseElevation(): Float
    fun getCardViewAt(position: Int): CardView?
    fun getCount(): Int
}