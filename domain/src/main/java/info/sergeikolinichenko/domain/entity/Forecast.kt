package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:30 (GMT+3) **/

data class Forecast(
  val forecastLocation: ForecastLocation,
  val forecastCurrent: ForecastCurrent,
  val upcomingDays: List<ForecastDaily>,
  val upcomingHours: List<ForecastHourly>
)
