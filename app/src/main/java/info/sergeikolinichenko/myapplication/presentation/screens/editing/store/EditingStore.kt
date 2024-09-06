package info.sergeikolinichenko.myapplication.presentation.screens.editing.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.favourite.SetOrderCitiesViewedUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingStore.State
import info.sergeikolinichenko.myapplication.utils.mapToCityFs
import info.sergeikolinichenko.myapplication.utils.toCityList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 14.08.2024 at 11:36 (GMT+3) **/

interface EditingStore : Store<Intent, State, Label> {

  @Parcelize
  data class State(
    val cities: CitiesStatus,
    val cityItems: List<CityItem>
  ) : Parcelable {
    @Parcelize
    data class CityItem(
      val id: Int,
      val temp: String,
      val icon: String,
    ) : Parcelable

    @Parcelize
    sealed interface CitiesStatus : Parcelable {
      @Parcelize
      data class CitiesLoaded(val cities: List<CityFs>) : CitiesStatus

      @Parcelize
      data object CitiesLoadingError : Parcelable, CitiesStatus

      @Parcelize
      data object CitiesInitial : Parcelable, CitiesStatus
    }
  }

  sealed interface Intent {
    data class ListOfCitiesChanged(val cities: List<CityFs>) : Intent
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
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val setOrderCitiesViewed: SetOrderCitiesViewedUseCase,
  private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase
) {

  fun create(cityItems: List<State.CityItem>): EditingStore =
    object : EditingStore, Store<Intent, State, Label> by storeFactory.create(
      name = "EditingStore",
      bootstrapper = BootstrapperImpl(),
      initialState = State(cities = State.CitiesStatus.CitiesInitial, cityItems = cityItems),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class FavouriteCitiesLoaded(val cities: List<City>) : Action
    data object FavouriteCitiesLoadedError : Action
  }

  private sealed interface Message {
    data class FavoriteCitiesLoaded(val cities: List<City>) : Message
    data object FavouriteCitiesLoadingError : Message
    data class ListOfCitiesChanged(val cities: List<CityFs>) : Message
  }

  private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {

      scope.launch {
        getFavouriteCities().collect { result ->
          when {
            result.isSuccess -> {
              result.getOrNull()?.let { cities ->
                dispatch(Action.FavouriteCitiesLoaded(cities))
              }
            }

            result.isFailure -> {
              dispatch(Action.FavouriteCitiesLoadedError)
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

        is Intent.ListOfCitiesChanged -> dispatch(Message.ListOfCitiesChanged(intent.cities))

        is Intent.OnDoneClicked -> {

          scope.launch {

            if (state().cities is State.CitiesStatus.CitiesLoaded) {

              val citiesFromScreen = (state().cities as State.CitiesStatus.CitiesLoaded).cities
              val citiesFromDb = getFavouriteCities.invoke().first().getOrNull()

              setOrderCitiesViewed(citiesFromScreen.map { it.id })

              if (citiesFromScreen == citiesFromDb) return@launch
              val citiesForDeletion = citiesFromDb?.filterNot {
                citiesFromScreen.toCityList().contains(it)
              }
              citiesForDeletion?.forEach { city ->
                changeFavouriteStateUseCase.removeFromFavourite(city.id)
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
        }

        Action.FavouriteCitiesLoadedError -> {
          dispatch(Message.FavouriteCitiesLoadingError)
        }
      }
    }
  }

  private object ReducerImpl : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State =
      when (msg) {
        is Message.FavoriteCitiesLoaded -> copy(cities = State.CitiesStatus.CitiesLoaded(msg.cities.map { city -> city.mapToCityFs() }))
        Message.FavouriteCitiesLoadingError -> copy(cities = State.CitiesStatus.CitiesLoadingError)
        is Message.ListOfCitiesChanged -> copy(cities = State.CitiesStatus.CitiesLoaded(msg.cities))
      }
  }
}