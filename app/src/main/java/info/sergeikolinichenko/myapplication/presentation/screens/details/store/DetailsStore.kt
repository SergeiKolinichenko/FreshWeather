package info.sergeikolinichenko.myapplication.presentation.screens.details.store

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:26 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetForecastUseCase
import info.sergeikolinichenko.domain.usecases.favourite.ObserveFavouriteStateUseCase
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.State
import info.sergeikolinichenko.myapplication.utils.toCity
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DetailsStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data object ChangeFavouriteStatusClicked : Intent
    data object OnBackClicked : Intent
  }

  data class State(
    val city: CityForScreen,
    val numberGradient: Int = 0,
    val isFavourite: Boolean,
    val forecastState: ForecastState
  ) {
    sealed interface ForecastState {
      data object Initial : ForecastState
      data object Loading : ForecastState
      data class Loaded(val forecast: Forecast) : ForecastState
      data object Error : ForecastState
    }
  }

  sealed interface Label {
    data object OnBackClicked : Label
  }
}

class DetailsStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getForecast: GetForecastUseCase,
  private val changeFavouriteState: ChangeFavouriteStateUseCase,
  private val observeFavouriteState: ObserveFavouriteStateUseCase
) {

  fun create(city: CityForScreen, numberGradient: Int): DetailsStore =
    object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
      name = "DetailsStore",
      initialState = State(
        city = city,
        numberGradient = numberGradient,
        isFavourite = false,
        forecastState = State.ForecastState.Initial
      ),
      bootstrapper = BootstrapperImpl(city),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class ChangeFavouriteState(val isFavourite: Boolean) : Action
    data class ForecastLoaded(val forecast: Forecast) : Action
    data object ForecastStartLoading : Action
    data object ForecastLoadingFailed : Action
  }

  private sealed interface Message {
    data class ChangeFavouriteState(val isFavourite: Boolean) : Message
    data class ForecastLoaded(val forecast: Forecast) : Message
    data object ForecastStartLoading : Message
    data object ForecastLoadingFailed : Message
  }

  private inner class BootstrapperImpl(
    private val city: CityForScreen
  ) : CoroutineBootstrapper<Action>() {
    override fun invoke() {
      scope.launch {
        observeFavouriteState(city.id).collect {
          dispatch(Action.ChangeFavouriteState(it))
        }
      }
      scope.launch {
        dispatch(Action.ForecastStartLoading)
        try {
          val forecast = getForecast(city.id)
          dispatch(Action.ForecastLoaded(forecast))
        } catch (e: Exception) {
          dispatch(Action.ForecastLoadingFailed)
        }
      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
      when (intent) {
        is Intent.ChangeFavouriteStatusClicked -> {
          scope.launch {

            val city = state().city //getState().city
            val isFavourite = state().isFavourite
            if (isFavourite) {
              changeFavouriteState.removeFromFavourite(city.id)
            } else {
              changeFavouriteState.addToFavourite(city.toCity())
            }
          }
        }

        is Intent.OnBackClicked -> {
          publish(Label.OnBackClicked)
        }
      }
    }

    override fun executeAction(action: Action) {
      when (action) {
        is Action.ChangeFavouriteState -> {
          dispatch(Message.ChangeFavouriteState(action.isFavourite))
        }

        is Action.ForecastLoaded -> {
          dispatch(Message.ForecastLoaded(action.forecast))
        }

        is Action.ForecastStartLoading -> {
          dispatch(Message.ForecastStartLoading)
        }

        is Action.ForecastLoadingFailed -> {
          dispatch(Message.ForecastLoadingFailed)
        }
      }
    }
  }

  private object ReducerImpl : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State =
      when (msg) {
        is Message.ChangeFavouriteState ->
          copy(isFavourite = msg.isFavourite)

        is Message.ForecastLoaded ->
          copy(forecastState = State.ForecastState.Loaded(msg.forecast))

        is Message.ForecastStartLoading ->
          copy(forecastState = State.ForecastState.Loading)

        is Message.ForecastLoadingFailed ->
          copy(forecastState = State.ForecastState.Error)
      }
  }
}
