package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:04 (GMT+3) **/

data class WeatherDto(
  @SerializedName("temp_c")
  val tempC: Float,

  @SerializedName("condition")
  val condition: ConditionDto
)
