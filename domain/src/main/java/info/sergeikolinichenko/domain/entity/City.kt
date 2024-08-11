package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:34 (GMT+3) **/

data class City(
  val id: Int,
  val name: String,
  val region: String,
  val country: String,
  val lat: Double,
  val lon: Double
)
