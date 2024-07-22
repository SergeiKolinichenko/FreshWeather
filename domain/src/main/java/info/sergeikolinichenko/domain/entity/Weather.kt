package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 30.03.2024 at 19:04 (GMT+3) **/

data class Weather(
  val temp: String,
  val maxTemp: String,
  val minTemp: String,
  val description: String,
  val condIconUrl: String
)
