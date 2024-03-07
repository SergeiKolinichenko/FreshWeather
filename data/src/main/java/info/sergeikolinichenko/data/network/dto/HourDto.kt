package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 05.03.2024 at 17:05 (GMT+3) **/

data class HourDto(
  @SerializedName("time_epoch")
  val hourTime: Long,

  @SerializedName("temp_c")
  val hourTemp: Float,

  @SerializedName("wind_kph")
  val hourWindKph: Float,

  @SerializedName("pressure_mb")
  val hourPressure: Float,

  @SerializedName("humidity")
  val hourHumidity: Int,

  @SerializedName("condition")
  val hourCond: Condition
)

data class Condition(
  @SerializedName("text")
  val description: String,
  val icon: String
)

//data class HourForecastDto(
//  val hourForecast: List<HourDto>
//)
