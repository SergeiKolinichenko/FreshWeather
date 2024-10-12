package info.sergeikolinichenko.myapplication.presentation.stors.settings

import android.content.Context
import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.SourceOfOpening
import kotlinx.parcelize.Parcelize

/** Created by Sergei Kolinichenko on 12.07.2024 at 21:29 (GMT+3) **/

interface SettingsStore: Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label> {

  sealed interface Intent {
    class ChangeOfTemperatureMeasure(val type: TEMPERATURE): Intent
    class ChangeOfPrecipitationMeasure(val type: PRECIPITATION): Intent
    class ChangeOfPressureMeasure(val type: PRESSURE): Intent
    class ChangeOfDaysOfWeather(val days: Int): Intent
    class OnClickedDone(val settings: Settings, val daysOfWeather: Int) : Intent
    class ClickedWriteDevelopers(val context: Context): Intent
    class OnClickedEvaluateApp(val context: Context) : Intent
    data object OnClickedBack: Intent
  }

  sealed interface Label {
    data class SettingsSaved(val sourceOfOpening: SourceOfOpening): Label
    data object OnBackClicked: Label
  }

  @Parcelize
  data class State(
    val temperature: TEMPERATURE,
    val precipitation: PRECIPITATION,
    val pressure: PRESSURE,
    val daysOfWeather: Int
  ): Parcelable
}