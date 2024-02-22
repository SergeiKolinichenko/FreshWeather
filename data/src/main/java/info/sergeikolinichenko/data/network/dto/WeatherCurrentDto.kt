package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:09 (GMT+3) **/

// This class is used to parse the current weather data from the API,
// it returned as a part of the WeatherForecastDto,
// returned by method getWeatherForecast in WeatherApiService
data class WeatherCurrentDto(
  @SerializedName("current")
  val current: WeatherDto
)
