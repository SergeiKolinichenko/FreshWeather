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

  @SerializedName("condition")
  val condition: ConditionDto
)
