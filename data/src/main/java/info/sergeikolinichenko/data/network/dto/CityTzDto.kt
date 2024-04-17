package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 13.04.2024 at 19:01 (GMT+3) **/

data class CityTzDto(
  @SerializedName("name")
  val nameCity: String,

  @SerializedName("region")
  val regionCity: String,

  @SerializedName("country")
  val countryCity: String,

  @SerializedName("tz_id")
  val idTimeZone: String
)

data class LocationInfo(
  @SerializedName("location")
  val locationInfo: CityTzDto
)
