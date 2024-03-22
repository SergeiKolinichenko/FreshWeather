package info.sergeikolinichenko.myapplication.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Created by Sergei Kolinichenko on 11.03.2024 at 15:30 (GMT+3) **/
@Parcelize
data class WeatherScreen(
  val temperature: Float,
  val maxTemp: Float?,
  val minTemp: Float?,
  val descriptionWeather: String,
  val conditionUrl: String,
  val windSpeed: Float,
  val windDirection: String,
  val airPressure: Float,
  val humidity: Int,
  val date: Long
): Parcelable
