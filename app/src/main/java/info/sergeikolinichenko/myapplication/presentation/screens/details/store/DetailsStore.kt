package info.sergeikolinichenko.myapplication.presentation.screens.details.store

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:26 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.favourite.ObserveFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetForecastUseCase
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.State
import info.sergeikolinichenko.myapplication.utils.toCity
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DetailsStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data object ChangeFavouriteStatusClicked : Intent
    data object OnBackClicked : Intent
  }

  data class State(
    val isFavourite: Boolean,
    val citiesState: CitiesState,
    val forecastState: ForecastState
  ) {
    sealed interface ForecastState {
      data object Initial : ForecastState
      data object Loading : ForecastState
      data class Loaded(val forecast: Forecast) : ForecastState
      data object Error : ForecastState
    }

    sealed interface CitiesState {
      data object Initial : CitiesState
      data class Loaded(
        val id: Int,
        val cities: List<CityForScreen>
      ) : CitiesState

      data object Error : CitiesState
    }
  }

  sealed interface Label {
    data object OnBackClicked : Label
  }
}

class DetailsStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getForecast: GetForecastUseCase,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val changeFavouriteState: ChangeFavouriteStateUseCase,
  private val observeFavouriteState: ObserveFavouriteStateUseCase
) {

  fun create(id: Int): DetailsStore =
    object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
      name = "DetailsStore",
      initialState = State(
        citiesState = State.CitiesState.Initial,
        isFavourite = false,
        forecastState = State.ForecastState.Initial
      ),
      bootstrapper = BootstrapperImpl(id),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class ChangeFavouriteState(val isFavourite: Boolean) : Action
    data class CitiesLoaded(val id: Int, val cities: List<CityForScreen>) : Action
    data object CitiesLoadingFailed : Action
  }

  private sealed interface Message {
    data class ChangeFavouriteState(val isFavourite: Boolean) : Message
    data class CitiesLoaded(val id: Int, val cities: List<CityForScreen>) : Message
    data object CitiesLoadingFailed : Message
    data class ForecastLoaded(val forecast: Forecast) : Message
    data object ForecastStartLoading : Message
    data class ForecastLoadingFailed(val referrerCode: String) : Message
  }

  private inner class BootstrapperImpl(
    private val id: Int
  ) : CoroutineBootstrapper<Action>() {

    override fun invoke() {

      scope.launch {

        getFavouriteCities().collect {

          dispatch(
            Action.CitiesLoaded(
              id = id,
              cities = it.getOrNull()!!.map { city -> city.toCityScreen() })
          )
        }
      }

      scope.launch {
        observeFavouriteState(id).collect {
          dispatch(Action.ChangeFavouriteState(it))
        }
      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    override fun executeIntent(intent: Intent) {
      when (intent) {

        is Intent.ChangeFavouriteStatusClicked -> {
          scope.launch {

            val cityState = state().citiesState as? State.CitiesState.Loaded

            val id = cityState?.id!!
            val city = cityState.cities[id]
            val isFavourite = state().isFavourite

            if (isFavourite) {
              changeFavouriteState.removeFromFavourite(id)
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

        is Action.CitiesLoaded -> {

          dispatch(
            Message.CitiesLoaded(
              id = action.id,
              cities = action.cities
            )
          )

          val city = action.cities.find { it.id == action.id }!!

          scope.launch {
            loadForecast(city.toCity())
          }
        }

        Action.CitiesLoadingFailed -> dispatch(Message.ForecastLoadingFailed("666"))
      }
    }

    private suspend fun loadForecast(city: City) {

      dispatch(Message.ForecastStartLoading)

      val result = getForecast(city)

      when {
        result.isSuccess -> {
          val forecast = result.getOrNull()!!
          dispatch(Message.ForecastLoaded(forecast))
        }

        result.isFailure -> {
          val errorCode = result.exceptionOrNull()!!.message!!
          dispatch(Message.ForecastLoadingFailed(errorCode))
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

        is Message.CitiesLoaded -> {
          copy(citiesState = State.CitiesState.Loaded(msg.id, msg.cities))
        }

        Message.CitiesLoadingFailed -> copy(forecastState = State.ForecastState.Error)
      }
  }
}
