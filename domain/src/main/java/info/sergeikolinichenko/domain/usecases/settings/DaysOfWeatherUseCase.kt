package info.sergeikolinichenko.domain.usecases.settings

import info.sergeikolinichenko.domain.repositories.SettingsRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 17.08.2024 at 15:50 (GMT+3) **/

class DaysOfWeatherUseCase @Inject constructor(
  private val repository: SettingsRepository
) {

  fun setDaysOfWeather(days: Int) = repository.setDaysOfWeather(days)
  fun getDaysOfWeather() = repository.getDaysOfWeather()

}