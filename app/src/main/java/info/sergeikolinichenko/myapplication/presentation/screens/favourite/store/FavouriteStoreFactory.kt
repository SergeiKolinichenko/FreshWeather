package info.sergeikolinichenko.myapplication.presentation.screens.favourite.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetForecastUseCase
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreen
import info.sergeikolinichenko.myapplication.utils.toCity
import info.sergeikolinichenko.myapplication.utils.toCityList
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 02.07.2024 at 19:07 (GMT+3) **/
class FavouriteStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
  private val searchCities: SearchCitiesUseCase,
  private val getForecast: GetForecastUseCase
) {
  fun create(): FavouriteStore =
    object : FavouriteStore,
      Store<FavouriteStore.Intent, FavouriteStore.State, FavouriteStore.Label> by storeFactory.create(
        name = "FavouriteStore",
        initialState = FavouriteStore.State(
          citiesState = FavouriteStore.State.CitiesState.Initial,
          weatherState = FavouriteStore.State.WeatherState.Initial,
        ),
        bootstrapper = BootstrapperImpl(),
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

    data class WeatherLoaded(val listForecasts: List<Forecast>) : Message

    data class WeatherLoadingError(val error: String?) : Message

    data object WeatherLoading : Message

    data object DropDownMenuOpened : Message

    data object DropDownMenuClosed : Message
  }

  private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {

      scope.launch {

        getFavouriteCities().collect { result ->
          when {
            result.isSuccess -> {

              result.getOrNull()?.let { cities ->

                cities.forEach { city ->

                  if (city.lat == 0.0 || city.lon == 0.0) {
                    cityInfoAdd(city, changeFavouriteStateUseCase, searchCities)
                  }

                }
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

  private inner class ExecutorImpl : CoroutineExecutor<FavouriteStore.Intent,
      Action,
      FavouriteStore.State,
      Message,
      FavouriteStore.Label>() {

    override fun executeIntent(intent: FavouriteStore.Intent) {

      when (intent) {
        is FavouriteStore.Intent.SearchClicked -> publish(FavouriteStore.Label.OnClickSearch)

        is FavouriteStore.Intent.ItemCityClicked ->
          publish(FavouriteStore.Label.OnClickCity(intent.id))

        FavouriteStore.Intent.ActionMenuClicked -> dispatch(Message.DropDownMenuOpened)
        FavouriteStore.Intent.ClosingActionMenu -> dispatch(Message.DropDownMenuClosed)

        FavouriteStore.Intent.ItemMenuSettingsClicked -> {
          publish(FavouriteStore.Label.OnClickItemMenuSettings)
          dispatch(Message.DropDownMenuClosed)
        }

        is FavouriteStore.Intent.ReloadWeather -> {

          val cities = state().citiesState as FavouriteStore.State.CitiesState.Loaded

          scope.launch {
            loadWeather(cities.listCities.toCityList())
          }
        }

        FavouriteStore.Intent.ItemMenuEditingClicked -> {

          if (state().citiesState !is FavouriteStore.State.CitiesState.Loaded) return
          if (state().weatherState !is FavouriteStore.State.WeatherState.Loaded) return

          val cities = state().citiesState as FavouriteStore.State.CitiesState.Loaded
          val weatherState = state().weatherState as FavouriteStore.State.WeatherState.Loaded

          val cityItems = cities.listCities.map { city ->
            EditingFavouritesStore.State.CityItem(
              id = city.id,
              temp = weatherState.listForecast.first { it.id == city.id }.currentForecast.temp,
              icon = weatherState.listForecast.first { it.id == city.id }.currentForecast.icon
            )
          }
          publish(
            FavouriteStore.Label.OnClickItemMenuEditing(
              cities = cityItems
            )
          )
          dispatch(Message.DropDownMenuClosed)
        }

        FavouriteStore.Intent.ReloadCities -> {
          scope.launch {
            getFavouriteCities().collect { result ->
              when {
                result.isSuccess -> {
                  result.getOrNull()?.let { cities ->
                    loadWeather(cities)
                  }
                }

                result.isFailure -> {
                  dispatch(Message.FavouriteCitiesLoadingError)
                }
              }
            }
          }
        }
      }
    }

    override fun executeAction(action: Action) {

      when (action) {

        is Action.FavouriteCitiesLoaded -> {

          val cities = action.cities

          scope.launch {
            loadWeather(cities)
          }
        }

        Action.FavouriteCitiesLoadedError -> dispatch(Message.FavouriteCitiesLoadingError)
      }
    }

    private suspend fun loadWeather(cities: List<City>) {

      dispatch(Message.FavoriteCitiesLoaded(cities))
      dispatch(Message.WeatherLoading)

      val result = getForecast.invoke(cities)

      when {
        result.isSuccess -> {
          val forecast = result.getOrNull()!!
          dispatch(Message.WeatherLoaded(listForecasts = forecast))
        }

        result.isFailure -> {
          dispatch(Message.WeatherLoadingError(result.exceptionOrNull()!!.message))
        }
      }

    }
  }

  private object ReducerImpl : Reducer<FavouriteStore.State, Message> {

    override fun FavouriteStore.State.reduce(msg: Message): FavouriteStore.State =
      when (msg) {

        is Message.FavoriteCitiesLoaded -> {

          copy(FavouriteStore.State.CitiesState.Loaded(
            listCities = msg.cities.map { it.toCityScreen() }))
        }

        is Message.WeatherLoaded -> {
          copy(weatherState = FavouriteStore.State.WeatherState.Loaded(
            listForecast = msg.listForecasts.map { it.mapToForecastScreen() }))
        }

        is Message.WeatherLoading -> {
          copy(weatherState = FavouriteStore.State.WeatherState.Loading)
        }

        is Message.WeatherLoadingError -> copy(
            weatherState = FavouriteStore.State.WeatherState.Error(
              errorMessage = msg.error ?: "Something went wrong"))

        Message.DropDownMenuClosed ->
          copy(dropDownMenuState = FavouriteStore.State.DropDownMenuState.CloseMenu)

        Message.DropDownMenuOpened ->
          copy(dropDownMenuState = FavouriteStore.State.DropDownMenuState.OpenMenu)

        Message.FavouriteCitiesLoadingError ->
          copy(citiesState = FavouriteStore.State.CitiesState.Error)
      }
  }
}

private suspend fun cityInfoAdd(
  city: City,
  changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
  searchCities: SearchCitiesUseCase,
) {
  val searchedCity = searchCities(city.name)

  if (searchedCity.isNotEmpty()) {
    changeFavouriteStateUseCase.addToFavourite(searchedCity.first())
  }
}