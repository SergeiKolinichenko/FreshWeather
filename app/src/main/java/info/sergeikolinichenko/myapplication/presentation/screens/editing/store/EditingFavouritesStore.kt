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
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore.State
import info.sergeikolinichenko.myapplication.utils.ORDER_LIST_CITIES_CHANGED
import info.sergeikolinichenko.myapplication.utils.ORDER_LIST_CITIES_NOT_CHANGED
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 14.08.2024 at 11:36 (GMT+3) **/

interface EditingFavouritesStore : Store<Intent, State, Label> {

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
    data class OnDoneClicked(val cities: List<CityFs>) : Intent
    data object OnBackClicked : Intent
  }

  sealed interface Label {
    data class OnDoneClicked(val orderChanged: Boolean) : Label
    data object OnBackClicked : Label
  }
}

class EditingFavouritesStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val setOrderCitiesViewed: SetOrderCitiesViewedUseCase,
  private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase
) {

  fun create(cityItems: List<State.CityItem>): EditingFavouritesStore =
    object : EditingFavouritesStore, Store<Intent, State, Label> by storeFactory.create(
      name = "EditingFavouritesStore",
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

        is Intent.OnDoneClicked -> {

          scope.launch {

            val citiesFromScreen = intent.cities
            val citiesFromDb = (state().cities as? State.CitiesStatus.CitiesLoaded)?.cities
            var orderChanged = ORDER_LIST_CITIES_NOT_CHANGED

            setOrderCitiesViewed(citiesFromScreen.map { it.id })

            citiesFromDb?.let { cityDb ->
              if (citiesFromScreen.size == cityDb.size) return@let
              else {
                val citiesInCitiesFromScreen = citiesFromScreen.toMutableSet()

                val citiesForDeletion = citiesFromDb.filterNot {
                  citiesInCitiesFromScreen.contains(it)
                }

                citiesForDeletion.forEach { city ->
                  changeFavouriteStateUseCase.removeFromFavourite(city.id)
                }
                orderChanged = ORDER_LIST_CITIES_CHANGED
              }
            }
            publish(Label.OnDoneClicked(orderChanged))
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
        is Message.FavoriteCitiesLoaded -> copy(cities = State.CitiesStatus.CitiesLoaded(msg.cities.map { city -> city.toCityScreen() }))
        Message.FavouriteCitiesLoadingError -> copy(cities = State.CitiesStatus.CitiesLoadingError)
      }
  }
}