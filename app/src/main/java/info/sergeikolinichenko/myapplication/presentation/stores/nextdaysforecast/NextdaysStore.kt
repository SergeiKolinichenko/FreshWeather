package info.sergeikolinichenko.myapplication.presentation.stores.nextdaysforecast

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastsFromNetUseCase
import info.sergeikolinichenko.domain.usecases.forecast.HandleForecastInDbUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.stores.nextdaysforecast.NextdaysStore.Intent
import info.sergeikolinichenko.myapplication.presentation.stores.nextdaysforecast.NextdaysStore.Label
import info.sergeikolinichenko.myapplication.presentation.stores.nextdaysforecast.NextdaysStore.State
import info.sergeikolinichenko.myapplication.utils.DoNeedNewOne
import info.sergeikolinichenko.myapplication.utils.mapCityFsListToCityList
import info.sergeikolinichenko.myapplication.utils.mapCityToCityFs
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreenList
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 11.08.2024 at 14:00 (GMT+3) **/

interface NextdaysStore : Store<Intent, State, Label> {
  sealed interface Intent {
    data object OnSwipeTop : Intent
    data object OnSwipeBottom : Intent
    data object OnSwipeLeft : Intent
    data object OnSwipeRight : Intent
    data object OnClickClose : Intent
    data class OnDayClicked(val index: Int) : Intent
  }

  data class State(
    val index: Int,
    val forecastState: ForecastState,
    val citiesState: CitiesState
  ) {
    sealed interface ForecastState {
      data object Initial : ForecastState
      data class Loaded(val forecasts: List<ForecastFs>) : ForecastState
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
    data object GoBack : Label
    data object OnClickedClose : Label
  }
}

class NextdaysStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val getForecast: GetForecastsFromNetUseCase,
  private val handleForecastIntoDb: HandleForecastInDbUseCase,
  private val doNeedNewOne: DoNeedNewOne
) {

  fun create(id: Int, index: Int): NextdaysStore =
    object : NextdaysStore, Store<Intent, State, Label> by storeFactory.create(
      name = "NextdaysStore",
      initialState = State(
        index = index,
        forecastState = State.ForecastState.Initial,
        citiesState = State.CitiesState.Initial
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
    data class ForecastLoaded(val forecasts: List<ForecastFs>) : Message
    data class ForecastLoadingFailed(val referrerCode: String) : Message

    data class OnDayClicked(val index: Int) : Message

    data class NewCityId(val id: Int) : Message
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
              cities = it.getOrNull()!!.map { city -> city.mapCityToCityFs() })
          )
        }
      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    override fun executeIntent(intent: Intent) {

      when (intent) {

        is Intent.OnSwipeTop -> {
          if (state().citiesState is State.CitiesState.Loaded &&
            state().forecastState is State.ForecastState.Loaded
          ) {
            val index = state().index
            if (index > 1) {
              dispatch(Message.OnDayClicked(index - 1))
            } else {
              publish(Label.GoBack)
            }
          }
        }

        is Intent.OnSwipeBottom -> {
          if (state().citiesState is State.CitiesState.Loaded &&
            state().forecastState is State.ForecastState.Loaded
          ) {

            val index = state().index
            val forecastState = state().forecastState as State.ForecastState.Loaded
            val citiesState = state().citiesState as State.CitiesState.Loaded
            val forecast = forecastState.forecasts.first { it.id == citiesState.id }
            val days = forecast.upcomingDays.size

            if (index < days - 1) {
              dispatch(Message.OnDayClicked(index + 1))
            }
          }
        }

        is Intent.OnDayClicked -> {
          dispatch(Message.OnDayClicked(intent.index))
        }

        Intent.OnSwipeLeft -> {

          if (state().citiesState is State.CitiesState.Loaded) {

            val citiesState = state().citiesState as State.CitiesState.Loaded

            val cityIndex = citiesState.cities.indexOfFirst { it.id == citiesState.id }

            if (cityIndex > 0) {
              val id = citiesState.cities[cityIndex - 1].id

              dispatch(Message.NewCityId(id))
            }
          }
        }

        Intent.OnSwipeRight -> {

          if (state().citiesState is State.CitiesState.Loaded) {
            val citiesState = state().citiesState as State.CitiesState.Loaded
            val cityIndex = citiesState.cities.indexOfFirst { it.id == citiesState.id }

            if (cityIndex < citiesState.cities.size - 1) {
              val id = citiesState.cities[cityIndex + 1].id

              dispatch(Message.NewCityId(id))
            }
          }
        }

        Intent.OnClickClose -> publish(Label.OnClickedClose)
      }
    }

    override fun executeAction(action: Action) {
      when (action) {
        is Action.CitiesLoaded -> {
          dispatch(Message.CitiesLoaded(id = action.id, cities = action.cities))
          if (state().forecastState is State.ForecastState.Initial) {
            scope.launch {
              handleForecastIntoDb.getForecastsFromDb().collect {
                if (it.isSuccess) {
                  it.getOrNull()?.let { forecasts ->
                    val forecast = forecasts.first { it.id == action.id }
                    val ifForecastCorrect =
                      doNeedNewOne.invoke(forecast.currentForecast.date, forecast.tzId)
                    if (ifForecastCorrect) {
                      scope.launch {
                        loadForecastFromNet(action.cities.mapCityFsListToCityList())
                      }
                    } else {
                      dispatch(Message.ForecastLoaded(forecasts.mapToForecastScreenList()))
                    }
                  }
                }
              }
            }
          }
        }

        Action.CitiesLoadingFailed -> dispatch(Message.CitiesLoadingFailed)
      }
    }

    private suspend fun loadForecastFromNet(cities: List<City>) {

      val result = getForecast(cities)

      when {
        result.isSuccess -> {
          val forecast = result.getOrNull()!!
          handleForecastIntoDb.insertForecastToDb(forecast)
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

        is Message.CitiesLoaded -> {
          copy(citiesState = State.CitiesState.Loaded(msg.id, msg.cities))
        }

        Message.CitiesLoadingFailed -> copy(citiesState = State.CitiesState.Error)

        is Message.OnDayClicked -> copy(index = msg.index)

        is Message.ForecastLoaded -> {
          copy(forecastState = State.ForecastState.Loaded(msg.forecasts))
        }

        is Message.ForecastLoadingFailed -> copy(forecastState = State.ForecastState.Error)

        is Message.NewCityId -> {
          val cities = citiesState as State.CitiesState.Loaded
          copy(citiesState = State.CitiesState.Loaded(id = msg.id, cities = cities.cities))
        }
      }

  }
}