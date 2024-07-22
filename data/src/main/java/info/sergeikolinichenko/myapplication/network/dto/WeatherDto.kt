package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:09 (GMT+3) **/

// This class is used to parse the current weather data from the API,
// it returned as a part of the ForecastDto,
// returned by method getWeatherForecast in WeatherApiService
data class  WeatherDto(
  @SerializedName("current")
  val current: WeatherCurrentDto,

  @SerializedName("forecast")
  val weather: WeatherDaysDto
)

data class WeatherCurrentDto(
  @SerializedName("temp_c")
  val tempC: Float,

  @SerializedName("temp_f")
  val tempF: Float,

  @SerializedName("condition")
  val condition: ConditionDto
)

data class WeatherDaysDto(
  @SerializedName("forecastday")
  val forecastDay: List<WeatherDailyDto>,
)

data class WeatherDailyDto(
  @SerializedName("day")
  val day: WeatherDayDto
)

data class WeatherDayDto(
  @SerializedName("maxtemp_c")
  val maxTempC: Float,

  @SerializedName("maxtemp_f")
  val maxTempF: Float,

  @SerializedName("mintemp_c")
  val minTempC: Float,

  @SerializedName("mintemp_f")
  val minTempF: Float,
)

