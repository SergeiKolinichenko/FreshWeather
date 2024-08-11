package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 30.03.2024 at 22:18 (GMT+3) **/

data class DayForecast(
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
)
