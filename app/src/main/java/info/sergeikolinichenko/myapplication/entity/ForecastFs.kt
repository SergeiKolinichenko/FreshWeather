package info.sergeikolinichenko.myapplication.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Created by Sergei Kolinichenko on 11.08.2024 at 17:27 (GMT+3) **/

@Parcelize
data class ForecastFs(
  val id: Int,
  val tzId: String,
  val currentForecast: CurrentForecastFs,
  val upcomingDays: List<DayForecastFs>,
  val upcomingHours: List<HourForecastFs>
): Parcelable

@Parcelize
data class CurrentForecastFs(
  val date: Long,
  val temp: String,
  val feelsLike: String,
  val cloudCover: Float,
  val windDir: Float,
  val windSpeed: Float,
  val pressure: String,
  val humidity: Float,
  val precipProb: Float,
  val precip: String,
  val precipType: List<String>?,
  val uvIndex: Int,
  val conditions: String,
  val icon: String
): Parcelable

@Parcelize
data class DayForecastFs(
  val date: Long,
  val temp: String,
  val tempMax: String,
  val tempMin: String,
  val humidity: Float,
  val windSpeed: Float,
  val windDir: Float,
  val pressure: String,
  val uvIndex: Int,
  val cloudCover: Float,
  val precipProb: Float,
  val precip: String,
  val precipType: List<String>?,
  val description: String,
  val icon: String,
  val sunrise: Long,
  val sunset: Long,
  val moonrise: Long,
  val moonset: Long,
  val moonPhase: Float
): Parcelable

@Parcelize
data class HourForecastFs(
  val date: Long,
  val temp: String,
  val icon: String,
  val pressure: String,
  val humidity: Float,
  val uvIndex: Int,
  val precipProb: Float,
  val precipType: List<String>?,
): Parcelable
