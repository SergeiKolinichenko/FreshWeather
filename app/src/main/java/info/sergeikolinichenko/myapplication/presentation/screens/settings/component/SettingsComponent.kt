package info.sergeikolinichenko.myapplication.presentation.screens.settings.component

import android.content.Context
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.presentation.screens.settings.store.SettingsStore
import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 12.07.2024 at 19:02 (GMT+3) **/

interface SettingsComponent {

  val model: StateFlow<SettingsStore.State>

  fun setTemperaturesType(type: TEMPERATURE)

  fun setPrecipitationType(type: PRECIPITATION)

  fun setPressureType(type: PRESSURE)

  fun setDaysOfWeather(days: Int)

  fun onClickedEvaluateApp(context: Context)

  fun onClickedWriteDevelopers(context: Context)

  fun onClickedSaveSettings()

  fun onClickedBack()

}