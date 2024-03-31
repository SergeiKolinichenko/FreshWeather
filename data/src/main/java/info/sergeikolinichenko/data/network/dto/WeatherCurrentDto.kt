package info.sergeikolinichenko.data.network.dto

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:09 (GMT+3) **/

// This class is used to parse the current weather data from the API,
// it returned as a part of the WeatherForecastDto,
// returned by method getWeatherForecast in WeatherApiService
data class  WeatherCurrentDto(
  val current: WeatherDto
)
