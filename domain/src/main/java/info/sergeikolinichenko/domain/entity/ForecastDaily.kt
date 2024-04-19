package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 30.03.2024 at 22:18 (GMT+3) **/

data class ForecastDaily(
  val date: Long,
  val maxTempC: Float,
  val minTempC: Float,
  val condIconUrl: String,
  val windKph: Float,
  val uv: Float,
  val dailyWillTtRain: Int,
  val dailyChanceOfRain: Int,
  val dailyWillItSnow: Int,
  val dailyChanceOfSnow: Int
)
