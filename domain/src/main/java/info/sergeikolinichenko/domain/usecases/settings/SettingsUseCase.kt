package info.sergeikolinichenko.domain.usecases.settings

import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.repositories.SettingsRepository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 11.07.2024 at 12:06 (GMT+3) **/

class SettingsUseCase @Inject constructor(
  private val repository: SettingsRepository
) {
  fun setSettings(settings: Settings) =
    repository.setSettings(settings)

  fun getSettings() = repository.getSettings()

}