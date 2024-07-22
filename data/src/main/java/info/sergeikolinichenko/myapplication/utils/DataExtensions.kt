package info.sergeikolinichenko.myapplication.utils

import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 15.07.2024 at 20:31 (GMT+3) **/

fun Float.toCelsiusString(): String = "${this.roundToInt()}°C"
fun Float.toFahrenheitString(): String = "${this.roundToInt()}°F"