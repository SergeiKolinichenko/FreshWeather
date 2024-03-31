package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 30.03.2024 at 22:18 (GMT+3) **/

data class DailyWeather(
  val maxTempC: Float,
  val minTempC: Float,
  val condIconUrl: String,
  val windKph: Float,
  val date: Long
)
