package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 28.11.2024 at 15:38 (GMT+3) **/

data class FoundDto(
  @SerializedName("results")
  val found: List<PlaceDto>,
)

data class PlaceDto(
  @SerializedName("place_id")
  val placeId: String,
  @SerializedName("country")
  val country: String,
  @SerializedName("state")
  val state: String,
  @SerializedName("city")
  val city: String,
  @SerializedName("lat")
  val lat: Double,
  @SerializedName("lon")
  val lon: Double,
)
