package info.sergeikolinichenko.myapplication.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:34 (GMT+3) **/
@Parcelize
data class CityFs(
  val id: Int,
  val name: String,
  val region: String,
  val country: String,
  val lat: Double,
  val lon: Double
): Parcelable
