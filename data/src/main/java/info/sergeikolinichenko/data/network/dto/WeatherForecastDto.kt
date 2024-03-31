package info.sergeikolinichenko.data.network.dto

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:24 (GMT+3) **/
// This class is used to parse the JSON response from the server
// returned by method getWeatherForecast in WeatherApiService
data class WeatherForecastDto(
  val current: FocastCurrentDto,
  val forecast: ForecastDto
)
