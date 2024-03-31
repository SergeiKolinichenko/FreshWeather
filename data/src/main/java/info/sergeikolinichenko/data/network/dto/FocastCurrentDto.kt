package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 31.03.2024 at 14:41 (GMT+3) **/

data class FocastCurrentDto(
  @SerializedName("last_updated_epoch")
  val lastUpdatedEpoch: Long,

  @SerializedName("temp_c")
  val tempC: Float,

  @SerializedName("feelslike_c")
  val feelsLikeC: Float,

  @SerializedName("wind_kph")
  val windKph: Float,

  @SerializedName("wind_dir")
  val windDir: String,

  @SerializedName("pressure_mb")
  val pressureMb: Float,

  @SerializedName("precip_mm")
  val precipMm: Float,

  val cloud: Int,

  val uv: Float,

  val humidity: Int,

  val condition: ConditionDto
)
