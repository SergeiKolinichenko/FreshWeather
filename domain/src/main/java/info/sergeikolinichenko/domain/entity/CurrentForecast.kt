package info.sergeikolinichenko.domain.entity


/** Created by Sergei Kolinichenko on 30.03.2024 at 21:46 (GMT+3) **/

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
