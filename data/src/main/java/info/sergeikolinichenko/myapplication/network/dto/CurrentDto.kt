package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:09 (GMT+3) **/

// This class is used to parse the current weather data from the API,
// it returned as a part of the ForecastDto,
// returned by method getWeatherForecast in WeatherApiService
data class  CurrentDto(
  @SerializedName("current")
  val current: CurrentWeatherDto
)
data class CurrentWeatherDto(
  @SerializedName("temp_c")
  val tempC: Float,

  @SerializedName("condition")
  val condition: ConditionDto
)
