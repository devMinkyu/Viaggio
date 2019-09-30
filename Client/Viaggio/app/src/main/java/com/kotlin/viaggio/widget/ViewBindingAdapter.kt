package com.kotlin.viaggio.widget

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["bgCornerRadius"])
fun setBackgroundCornerRadius(view: View?, cornerRadius:Float) {
    var mCornerRadius = cornerRadius
    view?.let {mView ->
        val drawable = mView.background
        mCornerRadius *= mView.resources.displayMetrics.density
        val gradientDrawable = GradientDrawableUtil.getGradientDrawable(drawable, mCornerRadius)
        mView.background = gradientDrawable
    }?:return
}

@BindingAdapter("imageResBinder")
fun setImageByRes(imageView: ImageView, @DrawableRes resId: Int) {
    imageView.setImageResource(resId)
}