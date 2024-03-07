package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:21 (GMT+3) **/

data class ForecastDto(
  @SerializedName("forecastday")
  val forecastDay: List<DayDto>,

)
