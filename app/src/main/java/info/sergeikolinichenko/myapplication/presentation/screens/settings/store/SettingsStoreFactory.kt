package info.sergeikolinichenko.myapplication.presentation.screens.settings.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.usecases.settings.SettingsUseCase
import info.sergeikolinichenko.myapplication.presentation.ui.content.settings.nonuifuns.evaluateApp
import info.sergeikolinichenko.myapplication.presentation.ui.content.settings.nonuifuns.writeSDevelopers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 13.07.2024 at 10:53 (GMT+3) **/

class SettingsStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val settingsUseCase: SettingsUseCase
) {

  fun create(): SettingsStore = object : SettingsStore,
    Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label>
    by storeFactory.create(
      name = "SettingsStore",
      initialState = SettingsStore.State(
        temperature = TEMPERATURE.CELSIUS,
        precipitation = PRECIPITATION.MM,
        pressure = PRESSURE.HPA
      ),
      bootstrapper = BootstrapperImpl(),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    class SettingsLoaded(val settings: Settings) : Action
  }

  private sealed interface Message {
    class SettingsLoaded(val settings: Settings) : Message
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

      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<SettingsStore.Intent,
      Action,
      SettingsStore.State,
      Message,
      SettingsStore.Label>() {
    override fun executeAction(action: Action) {
      when (action) {
        is Action.SettingsLoaded -> dispatch(Message.SettingsLoaded(settings = action.settings))
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
          settingsUseCase.setSettings(settings = intent.settings)
          publish(SettingsStore.Label.SettingsSaved)
        }

        SettingsStore.Intent.OnClickedBack ->
          publish(SettingsStore.Label.OnBackClicked)

        is SettingsStore.Intent.OnClickedEvaluateApp -> {
          intent.context.evaluateApp()
        }

        is SettingsStore.Intent.ClickedWriteDevelopers -> {
          val result = intent.context.writeSDevelopers()
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
      }
    }
  }
}