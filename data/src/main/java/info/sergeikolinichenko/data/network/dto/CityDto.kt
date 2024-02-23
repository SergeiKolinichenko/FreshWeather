package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:26 (GMT+3) **/
data class CityDto(
  @SerializedName("id")
  val id: Int,

  @SerializedName("name")
  val name: String,

  @SerializedName("region")
  val region: String,

  @SerializedName("country")
  val country: String,

  @SerializedName("url")
  val url: String,
)
