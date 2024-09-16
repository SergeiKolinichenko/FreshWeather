package info.sergeikolinichenko.myapplication.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import info.sergeikolinichenko.domain.entity.CurrentForecast
import info.sergeikolinichenko.domain.entity.DayForecast
import info.sergeikolinichenko.domain.entity.HourForecast

/** Created by Sergei Kolinichenko on 11.09.2024 at 18:32 (GMT+3) **/

internal class Converters {

  @TypeConverter
  fun fromStringList(value: List<String>?): String? {
    return Gson().toJson(value)
  }

  @TypeConverter
  fun toStringList(value: String?): List<String>? {
    val listType = object : TypeToken<List<String>>() {}.type
    return Gson().fromJson(value, listType)
  }

  @TypeConverter
  fun fromDayForecastList(value: List<DayForecast>?): String? {
    return Gson().toJson(value)
  }

  @TypeConverter
  fun toDayForecastList(value: String?): List<DayForecast>? {
    val listType = object : TypeToken<List<DayForecast>>() {}.type
    return Gson().fromJson(value, listType)
  }

  @TypeConverter
  fun fromHourForecastList(value: List<HourForecast>?): String? {
    return Gson().toJson(value)
  }

  @TypeConverter
  fun toHourForecastList(value: String?): List<HourForecast>? {
    val listType = object : TypeToken<List<HourForecast>>() {}.type
    return Gson().fromJson(value, listType)
  }

  @TypeConverter
  fun fromCurrentForecast(value: CurrentForecast): String {
    return Gson().toJson(value)
  }

  @TypeConverter
  fun toCurrentForecast(value: String): CurrentForecast {
    return Gson().fromJson(value, CurrentForecast::class.java)
  }
}