package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:24 (GMT+3) **/
// This class is used to parse the JSON response from the server
// returned by method getWeatherForecast in WeatherApiService
data class WeatherForecastDto(
  @SerializedName("current")
  val current: FocastCurrentDto,

  @SerializedName("forecast")
  val forecast: ForecastDto
)
