package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:30 (GMT+3) **/

data class Forecast(
  val id: Int,
  val tzId: String,
  val currentForecast: CurrentForecast,
  val upcomingDays: List<DayForecast>,
  val upcomingHours: List<HourForecast>
)

data class CurrentForecast(
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
)

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

data class HourForecast(
  val date: Long,
  val temp: String,
  val icon: String,
  val pressure: String,
  val humidity: Float,
  val uvIndex: Int,
  val precipProb: Float,
  val precipType: List<String>?,
)
