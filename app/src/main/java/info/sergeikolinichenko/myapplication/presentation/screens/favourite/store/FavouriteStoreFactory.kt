package info.sergeikolinichenko.myapplication.presentation.screens.favourite.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetWeatherUseCase
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 02.07.2024 at 19:07 (GMT+3) **/
class FavouriteStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
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

    data class FavouriteCitiesUploaded(val cities: List<City>) : Action

    data object FavouriteCitiesUploadedError : Action

  }

  private sealed interface Message {

    data class FavoriteCityUploaded(val cities: List<City>) : Message

    data object FavouriteCityLoadingError : Message

    data class CurrentWeatherLoaded(
      val cityId: Int,
      val temp: String,
      val maxTemp: String,
      val minTemp: String,
      val description: String,
      val iconUrl: String
    ) : Message

    data class WeatherLoadingError(val cityId: Int) : Message

    data class WeatherLoading(val cityId: Int) : Message

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
                dispatch(Action.FavouriteCitiesUploaded(cities))
              }
            }

            result.isFailure -> {
              dispatch(Action.FavouriteCitiesUploadedError)
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

        is FavouriteStore.Intent.ItemCityClicked -> publish(
          FavouriteStore.Label.OnClickCity(
            intent.city,
            intent.numberGradient
          )
        )

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
      }
    }

    override fun executeAction(action: Action) {

      when (action) {

        is Action.FavouriteCitiesUploaded -> {

          val cities = action.cities

          scope.launch {
            loadWeather(cities)
          }
        }

        Action.FavouriteCitiesUploadedError -> dispatch(Message.FavouriteCityLoadingError)
      }
    }

    private suspend fun loadWeather(cities: List<City>) {

      dispatch(Message.FavoriteCityUploaded(cities))

      cities.forEach {

        dispatch(Message.WeatherLoading(it.id))

        val result = getWeatherUseCase(it.id)

        when {
          result.isSuccess -> {
            val weather = result.getOrNull()!!
            dispatch(
              Message.CurrentWeatherLoaded(
                cityId = it.id,
                temp = weather.temp,
                maxTemp = weather.maxTemp,
                minTemp = weather.minTemp,
                description = weather.description,
                iconUrl = weather.condIconUrl
              )
            )
          }

          result.isFailure -> {
            dispatch(Message.WeatherLoadingError(it.id))
          }
        }
      }
    }
  }

  private object ReducerImpl : Reducer<FavouriteStore.State, Message> {
    override fun FavouriteStore.State.reduce(msg: Message): FavouriteStore.State =
      when (msg) {

        is Message.FavoriteCityUploaded -> {
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
                    iconUrl = msg.iconUrl
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
              if (cityItem.city.id == msg.cityId) {
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
              if (cityItem.city.id == msg.cityId) {
                cityItem.copy(
                  weatherLoadingState = FavouriteStore.State.WeatherLoadingState.Error
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

        Message.FavouriteCityLoadingError ->
          copy(listCitiesLoadedState = FavouriteStore.State.ListCitiesLoadedState.Error)
      }
  }
}