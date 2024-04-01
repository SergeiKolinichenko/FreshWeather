package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:18 (GMT+3) **/

data class DailyDto(
  @SerializedName("date_epoch")
  val dateEpoch: Long,

  @SerializedName("day")
  val dailyWeather: ForcastDailyDto,

  @SerializedName("hour")
  val forecastHourlyDtoArray: List<ForecastHourlyDto>
)
