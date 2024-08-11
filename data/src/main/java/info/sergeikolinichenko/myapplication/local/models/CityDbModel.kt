package info.sergeikolinichenko.myapplication.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:20 (GMT+3) **/
@Entity(tableName = "favourite_cities")
data class CityDbModel(
  @PrimaryKey
  val id: Int,
  val name: String,
  val region: String,
  val country: String,
  @ColumnInfo(defaultValue = "0.0")
  val lat: Double,
  @ColumnInfo(defaultValue = "0.0")
  val lon: Double
)
