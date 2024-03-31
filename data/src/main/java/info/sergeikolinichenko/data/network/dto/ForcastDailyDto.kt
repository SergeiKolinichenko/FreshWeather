package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:13 (GMT+3) **/

data class ForcastDailyDto(
  @SerializedName("time_epoch")
  val timeEpoch: Long,

  @SerializedName("maxtemp_c")
  val maxTempC: Float,

  @SerializedName("mintemp_c")
  val minTempC: Float,

  @SerializedName("maxwind_kph")
  val maxWindKph: Float,

  @SerializedName("daily_will_it_rain")
  val dailyWillTtRain: Int,

  @SerializedName("daily_chance_of_rain")
  val dailyChanceOfRain: Int,

  @SerializedName("daily_will_it_snow")
  val dailyWillItSnow: Int,

  @SerializedName("daily_chance_of_snow")
  val dailyChanceOfSnow: Int,

  @SerializedName("condition")
  val conditionDto: ConditionDto
)
