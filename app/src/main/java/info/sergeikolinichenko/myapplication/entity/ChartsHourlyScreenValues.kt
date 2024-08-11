package info.sergeikolinichenko.myapplication.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Created by Sergei Kolinichenko on 02.08.2024 at 10:25 (GMT+3) **/

@Parcelize
data class ChartsHourlyScreenValues(
  val date: Long,
  val pressureString: String,
  val pressureFloat: Float,
  val humidity: Int,
  val uv: Float,
  val icon: String
): Parcelable
