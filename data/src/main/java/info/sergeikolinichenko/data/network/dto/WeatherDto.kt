package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:04 (GMT+3) **/

data class WeatherDto(
  @SerializedName("last_updated_epoch")
  val date: Long,

  @SerializedName("temp_c")
  val temperatureC: Float,

  @SerializedName("temp_f")
  val temperatureF: Float,

  @SerializedName("feelslike_c")
  val feelsLikeC: Float,

  @SerializedName("feelslike_f")
  val feelsLikeF: Float,

  @SerializedName("wind_kph")
  val windSpeed: Float,

  @SerializedName("wind_dir")
  val windDirection: String,

  @SerializedName("pressure_mb")
  val airPressure: Float,

  @SerializedName("precip_mm")
  val precipiceMm: Float,

  @SerializedName("precip_in")
  val precipitationIn: Float,

  @SerializedName("cloud")
  val cloudCover: Int,

  @SerializedName("uv")
  val uvIndex: Float,

  val humidity: Int,

  @SerializedName("condition")
  val condition: ConditionDto
)
