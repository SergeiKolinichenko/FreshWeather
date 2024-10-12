package info.sergeikolinichenko.myapplication.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 15.07.2024 at 20:31 (GMT+3) **/

internal fun Float.toCelsiusString(): String = "${this.roundToInt()}°C"

internal fun Float.toFahrenheitString(): String = "${(this * 1.8f + 32).roundToInt()}°F"

internal fun Float.toMmsString(): String = "${this.roundToInt()} mm"

internal fun Float.tiInchString(): String = "${this.roundToInt()} in"

internal fun Float.toHpaString(): String = "${this.roundToInt()}\n hPa"

internal fun Float.toMmHgString(): String = "${(this * 0.750062).roundToInt()}\n mmHg"

//@TypeConverter
//internal fun List<String>.toStringFromStringList() = Gson().toJson(this)
//
//@TypeConverter
//internal fun String.toStringListFromString(): List<String> {
//  val listType = object : TypeToken<List<String>>() {}.type
//  return Gson().fromJson(this, listType)
//}