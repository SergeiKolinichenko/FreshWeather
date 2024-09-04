package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 29.07.2024 at 17:10 (GMT+3) **/

data class ForecastDto(

  @SerializedName("timezone")
  val timeZone: String,

  @SerializedName("currentConditions")
  val currentWeatherDto: CurrentWeatherDto,

  @SerializedName("days")
  val daysForecast: List<DayForecastDto>
)

data class CurrentWeatherDto(
  @SerializedName("datetimeEpoch")
  val datetimeEpoch: Long,

  @SerializedName("temp")
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

  @SerializedName("pressure")
  val pressure: Float,

  @SerializedName("uvindex")
  val uvIndex: Int,

  @SerializedName("cloudcover")
  val cloudCover: Float,

  @SerializedName("conditions")
  val conditions: String,

  @SerializedName("icon")
  val icon: String,
)

data class DayForecastDto(
  @SerializedName("datetimeEpoch")
  val datetimeEpoch: Long,

  @SerializedName("temp")
  val temp: Float,

  @SerializedName("tempmax")
  val tempMax: Float,

  @SerializedName("tempmin")
  val tempMin: Float,

  @SerializedName("humidity")
  val humidity: Float,

  @SerializedName("windspeed")
  val windSpeed: Float,

  @SerializedName("winddir")
  val windDir: Float,

  @SerializedName("pressure")
  val pressure: Float,

  @SerializedName("uvindex")
  val uvIndex: Int,

  @SerializedName("cloudcover")
  val cloudCover: Float,

  @SerializedName("precipprob")
  val precipProb: Float,

  @SerializedName("precip")
  val precip: Float,

  @SerializedName("preciptype")
  val precipType: List<String>?,

  @SerializedName("description")
  val description: String,

  @SerializedName("icon")
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

  @SerializedName("temp")
  val temp: Float,

  @SerializedName("precipprob")
  val precipProb: Float,

  @SerializedName("preciptype")
  val precipType: List<String>?,

  @SerializedName("humidity")
  val humidity: Float,

  @SerializedName("pressure")
  val pressure: Float,

  @SerializedName("uvindex")
  val uvIndex: Int,

  @SerializedName("icon")
  val icon: String,
)
