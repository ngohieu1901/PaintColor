package com.paintcolor.drawing.paint.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.time.times

@SuppressLint("SimpleDateFormat")
fun Long.toDateTime(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return sdf.format(date)
}

fun Long.toDate(pattern: String = "dd/MM/yyyy", locale: Locale = Locale.getDefault()): String {
    val sdf = SimpleDateFormat(pattern, locale)
    return sdf.format(Date(this))
}

fun Float.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

fun String.isNumber(): Boolean {
    return try {
        this.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun Any.isDouble(): Boolean {
    return this is Double
}

fun Float.toRadian(): Float {
    return this * (PI / 180).toFloat()
}
fun Float.roundToFive(): Int {
    return ((this / 5.0).roundToInt() * 5.0).toInt()
}
fun Int.roundToFive(): Int {
    return ((this / 5.0).roundToInt() * 5.0).toInt()
}

fun Double.toDMSLatitude(): String {
    val degrees = this.toInt()
    val minutes = ((this - degrees) * 60).toInt()
    val seconds = ((this - degrees - minutes / 60) * 60 * 60).toInt()
    return "${degrees}° ${minutes}' ${seconds}\"" + if (this >= 0) " N" else " S"
}

fun Double.toDMSLongitude(): String {
    val degrees = this.toInt()
    val minutes = ((this - degrees) * 60).toInt()
    val seconds = ((this - degrees - minutes / 60) * 60 * 60).toInt()
    return "${degrees}° ${minutes}' ${seconds}\"" + if (this >= 0) "E" else "W"
}
