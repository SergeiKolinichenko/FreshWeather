package info.sergeikolinichenko.myapplication.utils

import androidx.compose.ui.graphics.Color

/** Created by Sergei Kolinichenko on 26.07.2024 at 10:37 (GMT+3) **/

const val TITLE_ICON_SIZE_16 = 16
const val SYS_ICON_SIZE_24 = 24
const val WEATHER_ICON_SIZE_36 = 36
const val ORDER_LIST_CITIES_CHANGED = true
const val ORDER_LIST_CITIES_NOT_CHANGED = false

internal object LinearGradient {
  val gradientUvIndex = arrayOf(
    0.0f to Color(0xFF44D04B),
    0.34f to Color(0xFFF9C104),
    0.72f to Color(0xFFF7395B),
    1.0f to Color(0xFFD152DA)
  )

  val gradientDailyTemp = arrayOf(
    0.0f to Color(0xFF61C2F7),
    0.25f to Color(0xFF73D0BD),
    0.50f to Color(0xFFC1D39D),
    0.75f to Color(0xFFE8C33F),
    1.0f to Color(0xFFE96037)
  )

  val gradientUvIndexChart = listOf(
    Color(0xFFEF7C00),
    Color(0xFFD9D9D9)
  )

  val gradientHumidityChart = listOf(
    Color(0xFF2B74B8),
    Color(0xFFDDE7F5)
  )

  val gradientPressureChart = listOf(
    Color(0xFF2B74B8),
    Color(0xFFDDE7F5)
  )

}