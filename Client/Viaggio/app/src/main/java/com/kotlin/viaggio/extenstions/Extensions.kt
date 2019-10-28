package com.kotlin.viaggio.extenstions

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.source.LocalDataSource
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.common.BaseDialogFragment
import com.kotlin.viaggio.view.common.BaseFragment
import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.io.File
import java.util.*


fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}
fun GradientDrawable.setCornerRadius(
    topLeft: Float = 0F,
    topRight: Float = 0F,
    bottomRight: Float = 0F,
    bottomLeft: Float = 0F
) {
    cornerRadii = arrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    ).toFloatArray()
}

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ResourcesCompat.getDrawable(resources, drawable, null)
internal fun Context.getColorCompat(@ColorRes color: Int) = ResourcesCompat.getColor(resources, color, null)
internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))
internal fun Context.getColorStatList(@ColorRes color: Int) = ResourcesCompat.getColorStateList(resources, color, null)

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

fun AppCompatActivity.leftReplace(frag: BaseFragment<*>) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        .addToBackStack(null)
        .add(R.id.content_frame, frag, null).commit()
}
fun AppCompatActivity.topReplace(frag: BaseFragment<*>) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(
            R.anim.slide_in_up,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out_down
        )
        .addToBackStack(null)
        .add(R.id.content_frame, frag, null).commit()
}

fun Fragment.baseIntent(uri:String) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(uri)
    )
    intent.setPackage(BuildConfig.APPLICATION_ID)
    startActivity(intent)
}

fun Fragment.showDialog(frag: DialogFragment, tag: String) {
    val fragVal = parentFragmentManager.findFragmentByTag(tag)?.run {
        return
    }?:frag
    fragVal.show(parentFragmentManager, tag)
}

fun Context.imageName(imageName:String) :String {
    val image = imageName.split("/").last()
    val imageDir = File(filesDir, LocalDataSource.IMG_FOLDER)
    if(imageDir.exists().not()) {
        imageDir.mkdir()
    }
    return "$imageDir/$image"
}


