package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:13 (GMT+3) **/

data class DayWeatherDto(
  @SerializedName("maxtemp_c")
  val maxTemperatureC: Float,

  @SerializedName("maxtemp_f")
  val maxTemperatureF: Float,

  @SerializedName("mintemp_c")
  val minTemperatureC: Float,

  @SerializedName("mintemp_f")
  val minTemperatureF: Float,

  @SerializedName("avgtemp_c")
  val avgTemperatureC: Float,

  @SerializedName("avgtemp_f")
  val avgTemperatureF: Float,

  @SerializedName("condition")
  val conditionDto: ConditionDto
)
