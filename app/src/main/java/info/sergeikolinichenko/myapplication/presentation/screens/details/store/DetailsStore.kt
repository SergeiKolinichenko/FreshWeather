package info.sergeikolinichenko.myapplication.presentation.screens.details.store

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:26 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetForecastUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore.State
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreen
import info.sergeikolinichenko.myapplication.utils.toCity
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DetailsStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data class OnDayClicked(val id: Int, val index: Int, val forecast: ForecastFs) : Intent
    data object OnBackClicked : Intent
    data object OnSettingsClicked : Intent
    data object ReloadWeather : Intent
  }

  data class State(
    val citiesState: CitiesState,
    val forecastState: ForecastState
  ) {
    sealed interface ForecastState {
      data object Initial : ForecastState
      data object Loading : ForecastState
      data class Loaded(val forecast: ForecastFs) : ForecastState
      data object Error : ForecastState
    }

    sealed interface CitiesState {
      data object Initial : CitiesState
      data class Loaded(
        val id: Int,
        val cities: List<CityFs>
      ) : CitiesState

      data object Error : CitiesState
    }
  }

  sealed interface Label {
    data object OnBackClicked : Label
    data object OnSettingsClicked : Label
    data class OnDayClicked(val id: Int, val index: Int, val forecast: ForecastFs) : Label
  }
}

class DetailsStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getForecast: GetForecastUseCase,
  private val getFavouriteCities: GetFavouriteCitiesUseCase
) {

  fun create(id: Int): DetailsStore =
    object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
      name = "DetailsStore",
      initialState = State(
        citiesState = State.CitiesState.Initial,
        forecastState = State.ForecastState.Initial
      ),
      bootstrapper = BootstrapperImpl(id),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class CitiesLoaded(val id: Int, val cities: List<CityFs>) : Action
    data object CitiesLoadingFailed : Action
  }

  private sealed interface Message {
    data class CitiesLoaded(val id: Int, val cities: List<CityFs>) : Message
    data object CitiesLoadingFailed : Message
    data class ForecastLoaded(val forecast: ForecastFs) : Message
    data object ForecastStartLoading : Message
    data class ForecastLoadingFailed(val referrerCode: String) : Message
  }

  private inner class BootstrapperImpl(
    private val id: Int
  ) : CoroutineBootstrapper<Action>() {

    override fun invoke() {

      scope.launch {

        getFavouriteCities().collect {

          when {
            it.isSuccess -> {
              dispatch(
                Action.CitiesLoaded(
                  id = id,
                  cities = it.getOrNull()!!.map { city -> city.toCityScreen() })
              )
            }

            it.isFailure -> {
              dispatch(Action.CitiesLoadingFailed)
            }
          }
        }
      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    override fun executeIntent(intent: Intent) {
      when (intent) {

        is Intent.OnBackClicked -> {
          publish(Label.OnBackClicked)
        }

        is Intent.OnDayClicked -> publish(
          Label.OnDayClicked(
            id = intent.id,
            forecast = intent.forecast,
            index = intent.index
          )
        )

        Intent.OnSettingsClicked -> publish(Label.OnSettingsClicked)

        Intent.ReloadWeather -> {
          if (state().citiesState is State.CitiesState.Loaded) {
            scope.launch {
              val id = (state().citiesState as State.CitiesState.Loaded).id
              val cities = (state().citiesState as State.CitiesState.Loaded).cities
              val city = cities.find { it.id == id }!!
              loadForecast(city.toCity())
            }
          }
        }
      }
    }

    override fun executeAction(action: Action) {
      when (action) {

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
          dispatch(Message.ForecastLoaded(forecast.mapToForecastScreen()))
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

        is Message.ForecastLoaded ->
          copy(forecastState = State.ForecastState.Loaded(msg.forecast))

        is Message.ForecastStartLoading ->
          copy(forecastState = State.ForecastState.Loading)

        is Message.ForecastLoadingFailed ->
          copy(forecastState = State.ForecastState.Error)

        is Message.CitiesLoaded ->
          copy(citiesState = State.CitiesState.Loaded(msg.id, msg.cities))


        Message.CitiesLoadingFailed -> copy(citiesState = State.CitiesState.Error)
      }
  }
}
