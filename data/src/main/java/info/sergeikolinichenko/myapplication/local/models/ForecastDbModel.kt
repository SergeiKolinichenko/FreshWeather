package info.sergeikolinichenko.myapplication.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import info.sergeikolinichenko.domain.entity.CurrentForecast
import info.sergeikolinichenko.domain.entity.DayForecast
import info.sergeikolinichenko.domain.entity.HourForecast

/** Created by Sergei Kolinichenko on 11.09.2024 at 18:34 (GMT+3) **/

@Entity(tableName = "forecast")
data class ForecastDbModel(
  @PrimaryKey val id: Int,
  val tzId: String,
  val currentForecast: CurrentForecast,
  val upcomingDays: List<DayForecast>,
  val upcomingHours: List<HourForecast>
)
