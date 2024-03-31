package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 30.03.2024 at 22:27 (GMT+3) **/

data class HourlyWeather(
  val date: Long,
  val tempC: Float,
  val maxTempC: Float,
  val minTempC: Float,
  val descriptionText: String,
  val condIconUrl: String,
  val windKph: Float,
  val windDir: String,
  val pressureMb: Float,
  val humidity: Int,
  val willItRain: Int,
  val chanceOfRain: Int,
  val willItSnow: Int,
  val chanceOfSnow: Int,
)
