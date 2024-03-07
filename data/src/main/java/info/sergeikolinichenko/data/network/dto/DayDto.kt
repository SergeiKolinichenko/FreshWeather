package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:18 (GMT+3) **/

data class DayDto(
  @SerializedName("date_epoch")
  val date: Long,

  @SerializedName("day")
  val dayWeather: DayWeatherDto,

  @SerializedName("hour")
  val hourWeatherArray: List<HourDto>
)
