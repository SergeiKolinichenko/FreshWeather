package info.sergeikolinichenko.myapplication.presentation.stors.settings

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.usecases.settings.DaysOfWeatherUseCase
import info.sergeikolinichenko.domain.usecases.settings.SettingsUseCase
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.SourceOfOpening
import info.sergeikolinichenko.myapplication.presentation.ui.content.settings.nonuifuns.evaluateApp
import info.sergeikolinichenko.myapplication.presentation.ui.content.settings.nonuifuns.writeToDevelopers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 13.07.2024 at 10:53 (GMT+3) **/

class SettingsStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val settingsUseCase: SettingsUseCase,
  private val daysOfWeatherUseCase: DaysOfWeatherUseCase
) {

  fun create(sourceOfOpening: SourceOfOpening): SettingsStore = object : SettingsStore,
    Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label>
    by storeFactory.create(
      name = "SettingsStore",
      initialState = SettingsStore.State(
        temperature = TEMPERATURE.CELSIUS,
        precipitation = PRECIPITATION.MM,
        pressure = PRESSURE.HPA,
        daysOfWeather = 7
      ),
      bootstrapper = BootstrapperImpl(),
      executorFactory = { ExecutorImpl(sourceOfOpening = sourceOfOpening) },
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class SettingsLoaded(val settings: Settings) : Action
    data class DaysOfWeatherLoaded(val days: Int) : Action
  }

  private sealed interface Message {
    class SettingsLoaded(val settings: Settings) : Message
    data class DaysOfWeatherLoaded(val days: Int) : Message
    data class DaysOfWeatherChanged(val days: Int) : Message
    class ChangeOfTemperatureMeasure(val type: TEMPERATURE) : Message
    class ChangeOfPrecipitationMeasure(val type: PRECIPITATION) : Message
    class ChangeOfPressureMeasure(val type: PRESSURE) : Message
  }

  private object ReducerImpl : Reducer<SettingsStore.State, Message> {
    override fun SettingsStore.State.reduce(msg: Message): SettingsStore.State {

      return when (msg) {
        is Message.ChangeOfPrecipitationMeasure -> copy(precipitation = msg.type)
        is Message.ChangeOfPressureMeasure -> copy(pressure = msg.type)
        is Message.ChangeOfTemperatureMeasure -> copy(temperature = msg.type)
        is Message.SettingsLoaded -> copy(
          temperature = msg.settings.temperature,
          precipitation = msg.settings.precipitation,
          pressure = msg.settings.pressure
        )
        is Message.DaysOfWeatherLoaded -> copy(daysOfWeather = msg.days)
        is Message.DaysOfWeatherChanged -> copy(daysOfWeather = msg.days)
      }
    }
  }

  private inner class ExecutorImpl(private val sourceOfOpening: SourceOfOpening) : CoroutineExecutor<SettingsStore.Intent,
      Action,
      SettingsStore.State,
      Message,
      SettingsStore.Label>() {
    override fun executeAction(action: Action) {
      when (action) {
        is Action.SettingsLoaded -> dispatch(Message.SettingsLoaded(settings = action.settings))
        is Action.DaysOfWeatherLoaded -> dispatch(Message.DaysOfWeatherLoaded(days = action.days))
      }
    }

    override fun executeIntent(intent: SettingsStore.Intent) {
      when (intent) {

        is SettingsStore.Intent.ChangeOfPrecipitationMeasure -> {
          dispatch(Message.ChangeOfPrecipitationMeasure(type = intent.type))
        }

        is SettingsStore.Intent.ChangeOfPressureMeasure ->
          dispatch(Message.ChangeOfPressureMeasure(type = intent.type))

        is SettingsStore.Intent.ChangeOfTemperatureMeasure ->
          dispatch(Message.ChangeOfTemperatureMeasure(type = intent.type))

        is SettingsStore.Intent.OnClickedDone -> {
          daysOfWeatherUseCase.setDaysOfWeather(days = intent.daysOfWeather)
          settingsUseCase.setSettings(settings = intent.settings)
          publish(SettingsStore.Label.SettingsSaved(sourceOfOpening = sourceOfOpening))
        }

        SettingsStore.Intent.OnClickedBack ->
          publish(SettingsStore.Label.OnBackClicked)

        is SettingsStore.Intent.OnClickedEvaluateApp -> {
          intent.context.evaluateApp()
        }

        is SettingsStore.Intent.ClickedWriteDevelopers -> {
          val result = intent.context.writeToDevelopers()
        }

        is SettingsStore.Intent.ChangeOfDaysOfWeather ->
          if (intent.days in 0..14) {
            dispatch(Message.DaysOfWeatherChanged(days = intent.days))
          }
      }
    }
  }

  private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {
      scope.launch {
        settingsUseCase.getSettings().collect {
          dispatch(Action.SettingsLoaded(settings = it))
        }
        dispatch(Action.DaysOfWeatherLoaded(days = daysOfWeatherUseCase.getDaysOfWeather()))
      }
    }
  }
}