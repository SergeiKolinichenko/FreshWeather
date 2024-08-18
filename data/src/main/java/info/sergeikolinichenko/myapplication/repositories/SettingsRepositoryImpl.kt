package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 12.07.2024 at 20:19 (GMT+3) **/

class SettingsRepositoryImpl @Inject constructor(
  private val preferences: SharedPreferences
) : SettingsRepository {

  override fun setSettings(settings: Settings) {

    val jsonObject = Gson().toJson(settings)

    preferences.edit().putString(SETTINGS_KEY, jsonObject).apply()
  }

  override fun getSettings(): Flow<Settings> {

    val jsonObject = preferences.getString(SETTINGS_KEY, null)
    return jsonObject?.let {
      val settings = Gson().fromJson(jsonObject, Settings::class.java)
      flowOf(settings)
    } ?: flowOf(Settings(
      temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    ))
  }

  override fun setDaysOfWeather(days: Int) {
    preferences.edit().putInt(DAYS_OF_WEATHER_KEY, days).apply()
  }
  override fun getDaysOfWeather() = preferences.getInt(DAYS_OF_WEATHER_KEY, 7)

  companion object {
    const val SETTINGS_KEY = "settings"
    const val DAYS_OF_WEATHER_KEY = "days_of_weather"
  }
}