package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 30.03.2024 at 22:27 (GMT+3) **/

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
