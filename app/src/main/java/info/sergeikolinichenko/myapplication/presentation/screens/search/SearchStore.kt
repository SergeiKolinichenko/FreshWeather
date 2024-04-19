package info.sergeikolinichenko.myapplication.presentation.screens.search

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:55 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.SearchCitiesUseCase
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.presentation.screens.search.SearchStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.search.SearchStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.search.SearchStore.State
import info.sergeikolinichenko.myapplication.utils.toCity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchStore : Store<Intent, State, Label> {

  sealed interface Intent {
    data class SearchQueryChanged(val query: String) : Intent
    data class CityClicked(val city: CityScreen) : Intent
    data object ClickSearch : Intent
    data object ClickBack : Intent
  }

  data class State(
    val query: String,
    val state: SearchState
  ) {
    sealed interface SearchState {
      data object Initial : SearchState
      data object Loading : SearchState
      data object Error : SearchState
      data object Empty : SearchState
      data class SuccessLoaded(val cities: List<City>) : SearchState
    }
  }

  sealed interface Label {
    data object ClickBack : Label
    data object SavedToFavorite : Label
    data class OpenCityForecast(val city: CityScreen) : Label
  }
}

class SearchStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val searchCity: SearchCitiesUseCase,
  private val changeFavouriteState: ChangeFavouriteStateUseCase
) {

  fun create(openingOptions: OpeningOptions): SearchStore =
    object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
      name = "SearchStore",
      initialState = State(
        query = "",
        state = State.SearchState.Initial
      ),
      bootstrapper = BootstrapperImpl(),
      executorFactory = { ExecutorImpl(openingOptions) },
      reducer = ReducerImpl
    ) {}

  private sealed interface Action

  private sealed interface Message {
    data class SearchQuery(val query: String) : Message
    data class SearchResultLoaded(val cities: List<City>) : Message
    data object SearchResultLoading : Message
    data object SearchResultError : Message

  }

  private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {
    }
  }

  private inner class ExecutorImpl(private val openingOptions: OpeningOptions) :
    CoroutineExecutor<Intent, Action, State, Message, Label>() {
    private var job: Job? = null
    override fun executeIntent(intent: Intent, getState: () -> State) {
      when (intent) {
        is Intent.SearchQueryChanged -> dispatch(Message.SearchQuery(intent.query))
        is Intent.ClickBack -> publish(Label.ClickBack)
        is Intent.ClickSearch -> {

          job?.cancel()
          job = scope.launch {
            dispatch(Message.SearchResultLoading)
            try {

              val query = getState().query

              val cities = searchCity(query)

              dispatch(Message.SearchResultLoaded(cities))
            } catch (e: Exception) {
              dispatch(Message.SearchResultError)
            }
          }
        }

        is Intent.CityClicked -> {

          scope.launch {

            when (openingOptions) {
              OpeningOptions.ADD_TO_FAVORITES -> {
                scope.launch {
                  changeFavouriteState.addToFavourite(intent.city.toCity())
                  publish(Label.SavedToFavorite)
                }
              }

              OpeningOptions.ORDINARY_SEARCH ->
                publish(Label.OpenCityForecast(intent.city))
            }

          }
        }
      }

    }
  }

  private object ReducerImpl : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State =
      when (msg) {
        is Message.SearchQuery -> copy(query = msg.query)
        is Message.SearchResultLoading -> copy(state = State.SearchState.Loading)
        is Message.SearchResultError -> copy(state = State.SearchState.Error)
        is Message.SearchResultLoaded -> {
          if (msg.cities.isEmpty()) {
            copy(state = State.SearchState.Empty)
          } else {
            copy(state = State.SearchState.SuccessLoaded(msg.cities))
          }
        }
      }
  }
}
