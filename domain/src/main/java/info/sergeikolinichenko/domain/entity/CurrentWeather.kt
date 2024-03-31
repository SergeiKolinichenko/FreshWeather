package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 30.03.2024 at 21:46 (GMT+3) **/

data class CurrentWeather(
  val date: Long,
  val tempC: Float,
  val maxTempC: Float,
  val minTempC: Float,
  val feelsLikeC: Float,
  val cloud: Int,
  val precipMm: Float,
  val windDir: String,
  val windKph: Float,
  val pressureMb: Float,
  val humidity: Int,
  val uv: Float,
  val descriptionText: String,
  val condIconUrl: String,
)
