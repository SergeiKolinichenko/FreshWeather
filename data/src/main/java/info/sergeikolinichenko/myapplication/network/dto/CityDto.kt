package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:26 (GMT+3) **/
data class CityDto(
  @SerializedName("id")
  val idCity: Int,

  @SerializedName("name")
  val nameCity: String,

  @SerializedName("region")
  val regionCity: String,

  @SerializedName("country")
  val countryCity: String
)
