package info.sergeikolinichenko.myapplication.presentation.stores.editing

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesFromDbUseCase
import info.sergeikolinichenko.domain.usecases.favourite.SetOrderCitiesViewedUseCase
import info.sergeikolinichenko.domain.usecases.forecast.HandleForecastInDbUseCase
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStore.Intent
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStore.Label
import info.sergeikolinichenko.myapplication.presentation.stores.editing.EditingStore.State
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 14.08.2024 at 11:36 (GMT+3) **/

interface EditingStore : Store<Intent, State, Label> {

  @Parcelize
  data class State(
    val cityItems: List<CityItem>
  ) : Parcelable {

    @Parcelize
    data class CityItem(
      val id: Int,
      val name: String,
      val temp: String,
      val icon: String,
    ) : Parcelable
  }

  sealed interface Intent {
    data class ListOfCitiesChanged(val cityItems: List<State.CityItem>) : Intent
    data class RemoveItemFromListOfCities(val id: Int) : Intent
    data object OnDoneClicked : Intent
    data object OnBackClicked : Intent
  }

  sealed interface Label {
    data object OnDoneClicked : Label
    data object OnBackClicked : Label
  }
}

class EditingStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCities: GetFavouriteCitiesFromDbUseCase,
  private val handleForecastInDb: HandleForecastInDbUseCase,
  private val setOrderCitiesViewed: SetOrderCitiesViewedUseCase,
  private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase
) {

  fun create(): EditingStore =
    object : EditingStore, Store<Intent, State, Label> by storeFactory.create(
      name = "EditingStore",
      bootstrapper = BootstrapperImpl(),
      initialState = State(cityItems = emptyList()),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class FavouriteCitiesLoaded(val cities: List<City>) : Action
  }

  private sealed interface Message {
    data class FavoriteCitiesLoaded(val cities: List<City>) : Message
    data class ForecastsLoaded(val listForecasts: List<Forecast>) : Message
    data class ListOfCitiesChanged(val cityItems: List<State.CityItem>) : Message
    data class RemoveItemFromListOfCities(val id: Int) : Message
  }

  private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {

      scope.launch {
        getFavouriteCities().collect { result ->
          if (result.isSuccess) {
            result.getOrNull()?.let { cities ->
              dispatch(Action.FavouriteCitiesLoaded(cities))
            }
          }
        }
      }
    }

  }

  private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
      when (intent) {
        Intent.OnBackClicked -> publish(Label.OnBackClicked)

        is Intent.ListOfCitiesChanged -> dispatch(Message.ListOfCitiesChanged(intent.cityItems))

        is Intent.RemoveItemFromListOfCities -> dispatch(Message.RemoveItemFromListOfCities(intent.id))

        is Intent.OnDoneClicked -> {

          scope.launch {

            val citiesFromDb = getFavouriteCities.invoke().first().getOrNull() ?: return@launch

            setOrderCitiesViewed(state().cityItems.map { it.id })

            if (state().cityItems.size == citiesFromDb.size) {
              publish(Label.OnDoneClicked)
              return@launch
            }

            val deletionCityIds = citiesFromDb.filterNot { dbCity ->
              dbCity.id in state().cityItems.map { it.id }
            }.map { it.id }

            deletionCityIds.forEach {
              it.let { id ->
                changeFavouriteStateUseCase.removeFromFavourite(id)
              }
            }

            publish(Label.OnDoneClicked)
          }
        }
      }
    }

    override fun executeAction(action: Action) {
      when (action) {
        is Action.FavouriteCitiesLoaded -> {
          dispatch(Message.FavoriteCitiesLoaded(action.cities))

          scope.launch {
            val forecast = handleForecastInDb.getForecastsFromDb().first()
            if (forecast.isSuccess) {
              dispatch(Message.ForecastsLoaded(forecast.getOrNull()!!))
            }
          }
        }
      }
    }
  }

  private object ReducerImpl : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State =
      when (msg) {
        is Message.FavoriteCitiesLoaded -> {
          copy(
            cityItems = msg.cities.map {
              State.CityItem(
                id = it.id,
                name = it.name,
                temp = "",
                icon = ""
              )
            }
          )
        }

        is Message.ForecastsLoaded -> copy(
          cityItems = cityItems.map {
            val forecast = msg.listForecasts.find { forecast -> forecast.id == it.id }
            it.copy(
              temp = forecast?.currentForecast?.temp.toString(),
              icon = forecast?.currentForecast?.icon.toString()
            )
          }
        )

        is Message.ListOfCitiesChanged -> {
          /* It was done in this way because the simple method did not update the State when changing it. */
          copy(cityItems = cityItems.mapIndexed { index, cityItem ->
            val item = msg.cityItems[index]
            cityItem.copy(
              id = item.id,
              name = item.name,
              temp = item.temp,
              icon = item.icon
            )
          })
        }

        is Message.RemoveItemFromListOfCities -> {
          copy(cityItems = cityItems.filter { it.id != msg.id })
        }
      }
  }
}