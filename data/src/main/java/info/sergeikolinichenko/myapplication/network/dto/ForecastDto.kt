package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 29.07.2024 at 17:10 (GMT+3) **/

data class ForecastDto(

  @SerializedName("timezone")
  val timeZone: String,

  val description: String,
  @SerializedName("currentConditions")

  val currentWeatherDto: CurrentWeatherDto,

  @SerializedName("days")
  val daysForecast: List<DayForecastDto>
)

data class CurrentWeatherDto(
  @SerializedName("datetimeEpoch")
  val datetimeEpoch: Long,

  val temp: Float,

  @SerializedName("feelslike")
  val feelsLike: Float,

  val humidity: Float,

  @SerializedName("precipprob")
  val precipProb: Float,

  val precip: Float,

  @SerializedName("preciptype")
  val precipType: List<String>?,

  @SerializedName("windspeed")
  val windSpeed: Float,

  @SerializedName("winddir")
  val windDir: Float,

  val pressure: Float,

  @SerializedName("uvindex")
  val uvIndex: Int,

  @SerializedName("cloudcover")
  val cloudCover: Float,

  val conditions: String,

  val icon: String,
)

data class DayForecastDto(
  @SerializedName("datetimeEpoch")
  val datetimeEpoch: Long,

  val temp: Float,

  @SerializedName("tempmax")
  val tempMax: Float,

  @SerializedName("tempmin")
  val tempMin: Float,

  val humidity: Float,

  @SerializedName("windspeed")
  val windSpeed: Float,

  @SerializedName("winddir")
  val windDir: Float,

  val pressure: Float,

  @SerializedName("uvindex")
  val uvIndex: Int,

  @SerializedName("cloudcover")
  val cloudCover: Float,

  @SerializedName("precipprob")
  val precipProb: Float,

  val precip: Float,

  @SerializedName("preciptype")
  val precipType: List<String>?,

  val description: String,

  val icon: String,

  @SerializedName("sunriseEpoch")
  val sunrise: Long,

  @SerializedName("sunsetEpoch")
  val sunset: Long,

  @SerializedName("moonriseEpoch")
  val moonrise: Long,

  @SerializedName("moonsetEpoch")
  val moonset: Long,

  @SerializedName("moonphase")
  val moonPhase: Float,

  @SerializedName("hours")
  val hoursForecast: List<HourForecastDto>
)

data class HourForecastDto(
  @SerializedName("datetimeEpoch")
  val datetimeEpoch: Long,

  val temp: Float,

  @SerializedName("precipprob")
  val precipProb: Float,

  @SerializedName("preciptype")
  val precipType: List<String>?,

  val humidity: Float,

  val pressure: Float,

  @SerializedName("uvindex")
  val uvIndex: Int,

  val icon: String,
)
