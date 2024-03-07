package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:30 (GMT+3) **/

data class Forecast(
  val currentWeather: Weather,
  val upcomingDays: List<Weather>,
  val upcomingHours: List<Weather>
)
