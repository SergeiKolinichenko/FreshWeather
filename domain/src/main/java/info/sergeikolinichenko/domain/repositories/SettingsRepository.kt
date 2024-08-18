package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.Settings
import kotlinx.coroutines.flow.Flow

/** Created by Sergei Kolinichenko on 12.07.2024 at 20:15 (GMT+3) **/

interface SettingsRepository {

  fun setSettings(settings: Settings)
  fun getSettings(): Flow<Settings>

  fun setDaysOfWeather(days: Int)
  fun getDaysOfWeather(): Int

}