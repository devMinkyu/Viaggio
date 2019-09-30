package com.kotlin.viaggio.widget

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter

object GradientDrawableUtil {
    fun getGradientDrawable(drawable:Drawable, cornerRadius:Float):GradientDrawable {
        val gradientDrawable:GradientDrawable
        if(drawable is GradientDrawable) {
            gradientDrawable = drawable
        } else {
            gradientDrawable = GradientDrawable()
            if(drawable is ColorDrawable) {
                gradientDrawable.alpha = (drawable.alpha)
                gradientDrawable.setColor(drawable.color)
            }
        }
        gradientDrawable.cornerRadius = cornerRadius
        return gradientDrawable
    }
}