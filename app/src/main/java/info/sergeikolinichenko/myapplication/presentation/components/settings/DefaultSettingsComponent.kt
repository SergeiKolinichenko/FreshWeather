package info.sergeikolinichenko.myapplication.presentation.components.settings

import android.content.Context
import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.SourceOfOpening
import info.sergeikolinichenko.myapplication.presentation.stors.settings.SettingsStore
import info.sergeikolinichenko.myapplication.presentation.stors.settings.SettingsStoreFactory
import info.sergeikolinichenko.myapplication.utils.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultSettingsComponent @AssistedInject constructor(
  @Assisted("sourceOfOpening") private val sourceOfOpening: SourceOfOpening,
  @Assisted("exitSettings") private val onClickBack: () -> Unit,
  @Assisted("settingsSaved") private val settingsSaved: (sourceOfOpening: SourceOfOpening) -> Unit,
  @Assisted("componentContext") private val componentContext: ComponentContext,
  private val storeFactory: SettingsStoreFactory
) : SettingsComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore{ storeFactory.create(sourceOfOpening) }
  private val scope = componentScope()

  init {
    scope.launch {
      store.labels.collect {
        when (it) {
          is SettingsStore.Label.OnBackClicked -> onClickBack()
          is SettingsStore.Label.SettingsSaved -> settingsSaved(it.sourceOfOpening)
        }
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val model: StateFlow<SettingsStore.State>
    get() = store.stateFlow

  override fun setTemperaturesType(type: TEMPERATURE) {
    store.accept(SettingsStore.Intent.ChangeOfTemperatureMeasure(type = type))
  }

  override fun setPrecipitationType(type: PRECIPITATION) {
    store.accept(SettingsStore.Intent.ChangeOfPrecipitationMeasure(type = type))
  }

  override fun setPressureType(type: PRESSURE) {
    store.accept(SettingsStore.Intent.ChangeOfPressureMeasure(type = type))
  }

  override fun setDaysOfWeather(days: Int) {
    store.accept(SettingsStore.Intent.ChangeOfDaysOfWeather(days = days))
  }

  override fun onClickedEvaluateApp(context: Context) {
    store.accept(SettingsStore.Intent.OnClickedEvaluateApp(context = context))
  }

  override fun onClickedSaveSettings() {
    val settings = Settings(
      temperature = model.value.temperature,
      precipitation = model.value.precipitation,
      pressure = model.value.pressure
    )
    val days = model.value.daysOfWeather
    store.accept(SettingsStore.Intent.OnClickedDone(settings = settings, daysOfWeather = days))
  }

  override fun onClickedWriteDevelopers(context: Context) {
    store.accept(SettingsStore.Intent.ClickedWriteDevelopers(context = context))
  }

  override fun onClickedBack() {
    store.accept(SettingsStore.Intent.OnClickedBack)
  }

  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("sourceOfOpening") sourceOfOpening: SourceOfOpening,
      @Assisted("exitSettings") onClickBack: () -> Unit,
      @Assisted("settingsSaved") settingsSaved: (sourceOfOpening: SourceOfOpening) -> Unit,
      @Assisted("componentContext") componentContext: ComponentContext
    ): DefaultSettingsComponent
  }
}