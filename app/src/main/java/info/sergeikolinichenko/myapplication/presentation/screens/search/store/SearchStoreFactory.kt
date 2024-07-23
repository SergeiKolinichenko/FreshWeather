package info.sergeikolinichenko.myapplication.presentation.screens.search.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.search.store.SearchStore.State
import info.sergeikolinichenko.myapplication.utils.toCity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 22.07.2024 at 19:41 (GMT+3) **/

class SearchStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val searchCities: SearchCitiesUseCase,
  private val changeFavouriteState: ChangeFavouriteStateUseCase
) {

  fun create(): SearchStore =
    object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
      name = "SearchStore",
      initialState = State(
        query = "",
        state = State.SearchState.Initial
      ),
      bootstrapper = BootstrapperImpl(),
      executorFactory = { ExecutorImpl() },
      reducer = ReducerImpl
    ) {}

  private sealed interface Action

  private sealed interface Message {
    data class OnQueryChanged(val query: String) : Message
    data class SearchResultLoaded(val cities: List<City>) : Message
    data object SearchResultLoading : Message
    data object SearchResultError : Message
    data object NotEnoughLetters : Message
    data object OnClickedClearLine: Message
  }

  private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {
    }
  }

  private inner class ExecutorImpl :
    CoroutineExecutor<Intent, Action, State, Message, Label>() {
    private var job: Job? = null

    override fun executeIntent(intent: Intent) {
      when (intent) {

        is Intent.OnQueryChanged -> {

          dispatch(Message.OnQueryChanged(intent.query))

          val letterCount = intent.query.count { it.isLetter() }

          if (letterCount < 3) {

            dispatch(Message.NotEnoughLetters)

          } else {

            job?.cancel()

            job = scope.launch {

              dispatch(Message.SearchResultLoading)

              try {
                val query = state().query

                val cities = searchCities(query)

                dispatch(Message.SearchResultLoaded(cities))

              } catch (e: Exception) {
                dispatch(Message.SearchResultError)
              }
            }
          }
        }

        is Intent.OnClickedBack -> publish(Label.ClickedBack)

        is Intent.OnClickedClearLine -> dispatch(Message.OnClickedClearLine)

        is Intent.OnClickedCity -> {
          scope.launch {
            changeFavouriteState.addToFavourite(intent.city.toCity())
            publish(Label.SavedToFavorite)
          }
        }

      }
    }
  }

  private object ReducerImpl : Reducer<State, Message> {

    override fun State.reduce(msg: Message): State =

      when (msg) {

        is Message.OnQueryChanged -> copy(query = msg.query)

        is Message.SearchResultLoading -> copy(state = State.SearchState.Loading)

        is Message.SearchResultError -> copy(state = State.SearchState.Error)

        is Message.SearchResultLoaded -> {

          if (msg.cities.isEmpty()) {
            copy(state = State.SearchState.Empty)
          } else {

            copy(state = State.SearchState.SuccessLoaded(msg.cities))
          }

        }

        Message.NotEnoughLetters -> copy(state = State.SearchState.NotEnoughLetters)

        Message.OnClickedClearLine -> {
          copy(query = "", state = State.SearchState.SuccessLoaded(emptyList()))
        }
      }
  }
}