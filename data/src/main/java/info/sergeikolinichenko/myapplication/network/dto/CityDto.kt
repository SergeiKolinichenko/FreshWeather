package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 07.09.2024 at 17:50 (GMT+3) **/

data class CityDto(
  @SerializedName("place_id")
  val id: Int,

  @SerializedName("display_name")
  val displayName: String,

  @SerializedName("lat")
  val lat: String,

  @SerializedName("lon")
  val lon: String,

  @SerializedName("class")
  val classType: String,

  @SerializedName("type")
  val type: String,

  @SerializedName("address")
  val placeAddress: PlaceAddressDto
)

data class PlaceAddressDto(
  @SerializedName("state")
  val state: String?,

  @SerializedName("city")
  val city: String?,

  @SerializedName("town")
  val town: String?,

  @SerializedName("village")
  val village: String?,

  @SerializedName("country")
  val country: String,
)




