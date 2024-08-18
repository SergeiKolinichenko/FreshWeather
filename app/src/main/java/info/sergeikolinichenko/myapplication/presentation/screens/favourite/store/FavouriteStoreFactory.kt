package info.sergeikolinichenko.myapplication.presentation.screens.favourite.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetWeatherUseCase
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingFavouritesStore
import info.sergeikolinichenko.myapplication.utils.toCity
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 02.07.2024 at 19:07 (GMT+3) **/
class FavouriteStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
  private val searchCities: SearchCitiesUseCase,
  private val getWeatherUseCase: GetWeatherUseCase
) {
  fun create(): FavouriteStore =
    object : FavouriteStore,
      Store<FavouriteStore.Intent, FavouriteStore.State, FavouriteStore.Label> by storeFactory.create(
        name = "FavouriteStore",
        initialState = FavouriteStore.State(cityItems = emptyList()),
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

    data class CurrentWeatherLoaded(
      val cityId: Int,
      val temp: String,
      val maxTemp: String,
      val minTemp: String,
      val description: String,
      val iconUrl: String
    ) : Message

    data class WeatherLoadingError(
      val city: City,
      val error: Throwable
    ) : Message

    data class WeatherLoading(val id: Int) : Message

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

          val cities = intent.cities

          scope.launch {
            loadWeather(cities)
          }
        }

        FavouriteStore.Intent.ItemMenuEditingClicked -> {

          val cityItems = state().cityItems.map { cityItem ->
            val id = cityItem.city.id
            val temp =
              (cityItem.weatherLoadingState as FavouriteStore.State.WeatherLoadingState.LoadedWeatherLoading).temp
            val icon = (cityItem.weatherLoadingState).icon
            EditingFavouritesStore.State.CityItem(
              id = id,
              temp = temp,
              icon = icon
            )
          }
          publish(FavouriteStore.Label.OnClickItemMenuEditing(
            cities = cityItems
          ))
          dispatch(Message.DropDownMenuClosed)
        }

        FavouriteStore.Intent.ReloadCities -> {
          scope.launch {
            getFavouriteCities().collect{ result ->
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

      cities.forEach { city ->

        dispatch(Message.WeatherLoading(city.id))

        val result = getWeatherUseCase.invoke(city)

        when {
          result.isSuccess -> {
            val weather = result.getOrNull()!!
            dispatch(
              Message.CurrentWeatherLoaded(
                cityId = city.id,
                temp = weather.temp,
                maxTemp = weather.maxTemp,
                minTemp = weather.minTemp,
                description = weather.description,
                iconUrl = weather.condIconUrl
              )
            )
          }

          result.isFailure -> {
            dispatch(Message.WeatherLoadingError(city, result.exceptionOrNull()!!))
          }
        }
      }
    }
  }

  private object ReducerImpl : Reducer<FavouriteStore.State, Message> {
    override fun FavouriteStore.State.reduce(msg: Message): FavouriteStore.State =
      when (msg) {

        is Message.FavoriteCitiesLoaded -> {
          copy(
            cityItems = msg.cities.map { city ->
              FavouriteStore.State.CityItem(
                city = city.toCityScreen(),
                weatherLoadingState = FavouriteStore.State.WeatherLoadingState.Initial
              )
            },
            listCitiesLoadedState = FavouriteStore.State.ListCitiesLoadedState.Loaded
          )
        }

        is Message.CurrentWeatherLoaded -> {
          copy(
            cityItems = cityItems.map { cityItem ->
              if (cityItem.city.id == msg.cityId) {
                cityItem.copy(
                  weatherLoadingState = FavouriteStore.State.WeatherLoadingState.LoadedWeatherLoading(
                    temp = msg.temp,
                    maxTemp = msg.maxTemp,
                    minTemp = msg.minTemp,
                    description = msg.description,
                    icon = msg.iconUrl
                  )
                )
              } else {
                cityItem
              }
            }
          )
        }

        is Message.WeatherLoading -> {
          copy(
            cityItems = cityItems.map { cityItem ->
              if (cityItem.city.id == msg.id) {
                cityItem.copy(
                  weatherLoadingState = FavouriteStore.State.WeatherLoadingState.Loading
                )
              } else {
                cityItem
              }
            }
          )
        }

        is Message.WeatherLoadingError -> {

          copy(
            cityItems = cityItems.map { cityItem ->

              if (cityItem.city.toCity() == msg.city) {

                cityItem.copy(
                  weatherLoadingState = FavouriteStore.State.WeatherLoadingState.Error(
                    cityName = msg.city.name,
                    codeError = msg.error.message ?: ""
                  )
                )
              } else {
                cityItem
              }
            }
          )
        }

        Message.DropDownMenuClosed ->
          copy(dropDownMenuState = FavouriteStore.State.DropDownMenuState.CloseMenu)

        Message.DropDownMenuOpened ->
          copy(dropDownMenuState = FavouriteStore.State.DropDownMenuState.OpenMenu)

        Message.FavouriteCitiesLoadingError ->
          copy(listCitiesLoadedState = FavouriteStore.State.ListCitiesLoadedState.Error)
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