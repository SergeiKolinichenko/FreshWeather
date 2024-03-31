package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 05.03.2024 at 17:05 (GMT+3) **/

data class ForecastHourlyDto(
  @SerializedName("time_epoch")
  val timeEpoch: Long,

  @SerializedName("temp_c")
  val tempC: Float,

  @SerializedName("wind_kph")
  val windKph: Float,

  @SerializedName("pressure_mb")
  val pressureMb: Float,

  val humidity: Int,

  @SerializedName("wind_dir")
  val windDir: String,

  @SerializedName("will_it_rain")
  val willItRain: Int,

  @SerializedName("chance_of_rain")
  val chanceOfRain: Int,

  @SerializedName("will_it_snow")
  val willItSnow: Int,

  @SerializedName("chance_of_snow")
  val chanceOfSnow: Int,

  val uv: Float,

  val condition: ConditionDto
)
