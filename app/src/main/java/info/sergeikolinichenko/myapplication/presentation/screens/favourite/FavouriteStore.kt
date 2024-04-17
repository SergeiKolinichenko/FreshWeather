package info.sergeikolinichenko.myapplication.presentation.screens.favourite

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:31 (GMT+3) **/

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import info.sergeikolinichenko.data.mappers.NON_TIME_ZONE_INFO
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.GetCityInfoUseCase
import info.sergeikolinichenko.domain.usecases.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.GetWeatherUseCase
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.FavouriteStore.Intent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.FavouriteStore.Label
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.FavouriteStore.State
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.launch
import javax.inject.Inject

interface FavouriteStore : Store<Intent, State, Label> {
  sealed interface Intent {
    data object SearchClicked : Intent
    data object ButtonClicked : Intent
    data class ItemClicked(
      val city: CityScreen,
      val numberGradient: Int
    ) : Intent
  }

  data class State(
    val cityItems: List<CityItem>
  ) {

    data class CityItem(
      val city: CityScreen,
      val weatherState: WeatherState
    )

    sealed interface WeatherState {
      data object Initial : WeatherState
      data object Loading : WeatherState
      data object Error : WeatherState
      data class LoadedWeather(
        val temperature: Float,
        val iconUrl: String
      ) : WeatherState
    }
  }

  sealed interface Label {
    data object ClickSearch : Label
    data object ClickToButton : Label
    data class OnClickCity(
      val city: CityScreen,
      val numberGradient: Int
    ) : Label
  }
}

class FavouriteStoreFactory @Inject constructor(
  private val storeFactory: StoreFactory,
  private val getFavouriteCities: GetFavouriteCitiesUseCase,
  // migrating the database from version 1 to version 2
  private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
  private val getCityInfoUseCase: GetCityInfoUseCase,
  // migrating the database from version 1 to version 2
  private val getWeatherUseCase: GetWeatherUseCase
) {

  fun create(): FavouriteStore =
    object : FavouriteStore, Store<Intent, State, Label> by storeFactory.create(
      name = "FavouriteStore",
      initialState = State(listOf()),
      bootstrapper = BootstrapperImpl(),
      executorFactory = ::ExecutorImpl,
      reducer = ReducerImpl
    ) {}

  private sealed interface Action {
    data class FavoriteCityLoaded(val cities: List<City>) : Action
  }

  private sealed interface Message {
    data class FavoriteCityLoaded(val cities: List<City>) : Message
    data class WeatherLoaded(
      val cityId: Int,
      val temperature: Float,
      val iconUrl: String
    ) : Message

    data class WeatherLoadingError(val cityId: Int) : Message
    data class WeatherLoading(val cityId: Int) : Message
  }

  private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
    override fun invoke() {
      scope.launch {
        getFavouriteCities().collect { listCities ->

          /** start migrating the database from version 1 to version 2 **/
          listCities.forEach {  city ->
            if (city.idTimeZone == "" || city.idTimeZone == NON_TIME_ZONE_INFO) {
              val cityTz = getCityInfoUseCase.invoke(city)
              changeFavouriteStateUseCase.addToFavourite(cityTz)
            }
          }
          /** end migrating the database from version 1 to version 2 **/

          dispatch(Action.FavoriteCityLoaded(listCities))
        }
      }
    }
  }

  private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
      when (intent) {
        is Intent.SearchClicked -> publish(Label.ClickSearch)
        is Intent.ButtonClicked -> publish(Label.ClickToButton)
        is Intent.ItemClicked -> publish(Label.OnClickCity(intent.city, intent.numberGradient))
      }
    }

    override fun executeAction(action: Action, getState: () -> State) {
      when (action) {
        is Action.FavoriteCityLoaded -> {
          val cities = action.cities
          dispatch(Message.FavoriteCityLoaded(cities))
          cities.forEach {
            scope.launch {
              loadCityWeather(it)
            }
          }
        }
      }
    }

    private suspend fun loadCityWeather(city: City) {
      dispatch(Message.WeatherLoading(city.id))

      try {
        val weather = getWeatherUseCase(city.id)
        dispatch(
          Message.WeatherLoaded(
            cityId = city.id,
            temperature = weather.tempC,
            iconUrl = weather.condIconUrl
          )
        )
      } catch (e: Exception) {
        Message.WeatherLoadingError(city.id)
      }
    }
  }

  private object ReducerImpl : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State =
      when (msg) {

        is Message.FavoriteCityLoaded -> {
          copy(
            cityItems = msg.cities.map { city ->
              State.CityItem(
                city = city.toCityScreen(),
                weatherState = State.WeatherState.Initial
              )
            }
          )

        }

        is Message.WeatherLoaded -> {
          copy(
            cityItems = cityItems.map { cityItem ->
              if (cityItem.city.id == msg.cityId) {
                cityItem.copy(
                  weatherState = State.WeatherState.LoadedWeather(
                    temperature = msg.temperature,
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
                  weatherState = State.WeatherState.Loading
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
                  weatherState = State.WeatherState.Error
                )
              } else {
                cityItem
              }
            }
          )
        }
      }
  }
}
