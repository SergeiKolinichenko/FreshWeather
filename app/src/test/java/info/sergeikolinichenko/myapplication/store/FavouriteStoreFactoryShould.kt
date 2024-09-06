package info.sergeikolinichenko.myapplication.store

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStoreFactory
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreenList
import info.sergeikolinichenko.myapplication.utils.test
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.testCityFs
import info.sergeikolinichenko.myapplication.utils.testForecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 02.07.2024 at 17:05 (GMT+3) **/

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteStoreFactoryShould : BaseUnitTestsRules() {

  // region constants
  private val factory = DefaultStoreFactory()
  private val getForecastUseCase = mock<GetForecastUseCase>()
  private val getFavouriteCitiesUseCase = mock<GetFavouriteCitiesUseCase>()
  private val changeFavouriteStateUseCase = mock<ChangeFavouriteStateUseCase>()
  private val searchCitiesUseCase = mock<SearchCitiesUseCase>()
  private val cities = listOf(testCity)
  private val listForecast = listOf(testForecast)
  private val exception = Exception("Something went wrong")
  // endregion constants

  private val SUT = FavouriteStoreFactory(
    factory,
    getFavouriteCitiesUseCase,
    changeFavouriteStateUseCase,
    searchCitiesUseCase,
    getForecastUseCase
  )

  @Test
  fun `check that when the class is created, it is executed by calling GetFavouriteCitiesUseCase`(): Unit =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      SUT.create()
      // Assert
      verify(getFavouriteCitiesUseCase, times(1)).invoke()
    }

  @Test
  fun `check that when a class is created, the favourite cities are loaded from DB`(): Unit =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      val testField = (store.state.citiesState as FavouriteStore.State.CitiesState.Loaded).listCities.first()
      // Assert
      assert(testField.toTestCityForTest() == testCity)
    }

  @Test
  fun `check that when a class is created, the favourite cities loaded and is state is loaded`(): Unit =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      val testField = store.state.citiesState
      // Assert
      assert(testField is FavouriteStore.State.CitiesState.Loaded)
    }

  @Test
  fun `check that when a class is created, the favourite cities are not loaded from DB cities state is error`(): Unit =
    runTest {
      // Arrange
      whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.failure(exception)))
      // Act
      val store = SUT.create()
      val testField = store.state.citiesState
      // Assert
      assert(testField == FavouriteStore.State.CitiesState.Error)
    }

  @Test
  fun `check that when the class is created, it is executed by calling GetWeatherUseCase`(): Unit =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      SUT.create()
      // Assert
      verify(getForecastUseCase, times(1)).invoke(cities)
    }

  @Test
  fun `check that when a class is created, the favourite cities weather are not loaded from Network`(): Unit =
    runTest {
      // Arrange
      whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
      whenever(getForecastUseCase.invoke(cities)).thenReturn(Result.failure(exception))
      // Act
      val store = SUT.create()
      val testField = store.state.forecastState
      // Assert
      assert(testField is FavouriteStore.State.ForecastState.Error)
    }

  @Test
  fun `check that when a class is created, the favourite cities weather are loaded from Network`(): Unit =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      val testField =
        store.state.forecastState as FavouriteStore.State.ForecastState.Loaded
      // Assert
      assert(testField.listForecast.first().id == testForecast.id)
      assert(testField.listForecast.first().tzId == testForecast.tzId)
      assert(testField.listForecast.first().currentForecast.date == testForecast.currentForecast.date)
      assert(testField.listForecast.first().upcomingDays.first().date == testForecast.upcomingDays.first().date)
      assert(testField.listForecast.first().upcomingHours.first().date == testForecast.upcomingHours.first().date)
      assert(testField.listForecast.first().upcomingHours.first().temp == testForecast.upcomingHours.first().temp)
      assert(testField.listForecast.first().upcomingHours.first().icon == testForecast.upcomingHours.first().icon)
      assert(testField.listForecast.first().upcomingHours.first().pressure == testForecast.upcomingHours.first().pressure)
      assert(testField.listForecast.first().upcomingHours.first().humidity == testForecast.upcomingHours.first().humidity)
      assert(testField.listForecast.first().upcomingHours.first().uvIndex == testForecast.upcomingHours.first().uvIndex)
      assert(testField.listForecast.first().upcomingHours.first().precipProb == testForecast.upcomingHours.first().precipProb)
      assert(testField.listForecast.first().upcomingHours.first().precipType == testForecast.upcomingHours.first().precipType)
      assert(testField.listForecast.first().currentForecast.temp == testForecast.currentForecast.temp)
      assert(testField.listForecast.first().currentForecast.feelsLike == testForecast.currentForecast.feelsLike)
      assert(testField.listForecast.first().currentForecast.cloudCover == testForecast.currentForecast.cloudCover)
      assert(testField.listForecast.first().currentForecast.windDir == testForecast.currentForecast.windDir)
      assert(testField.listForecast.first().currentForecast.windSpeed == testForecast.currentForecast.windSpeed)
      assert(testField.listForecast.first().currentForecast.pressure == testForecast.currentForecast.pressure)
      assert(testField.listForecast.first().currentForecast.humidity == testForecast.currentForecast.humidity)
      assert(testField.listForecast.first().currentForecast.precipProb == testForecast.currentForecast.precipProb)
      assert(testField.listForecast.first().currentForecast.precip == testForecast.currentForecast.precip)
      assert(testField.listForecast.first().currentForecast.precipType == testForecast.currentForecast.precipType)
      assert(testField.listForecast.first().currentForecast.uvIndex == testForecast.currentForecast.uvIndex)
      assert(testField.listForecast.first().currentForecast.conditions == testForecast.currentForecast.conditions)
      assert(testField.listForecast.first().currentForecast.icon == testForecast.currentForecast.icon)
      assert(testField.listForecast.first().upcomingDays.first().temp == testForecast.upcomingDays.first().temp)
      assert(testField.listForecast.first().upcomingDays.first().tempMax == testForecast.upcomingDays.first().tempMax)
      assert(testField.listForecast.first().upcomingDays.first().tempMin == testForecast.upcomingDays.first().tempMin)
      assert(testField.listForecast.first().upcomingDays.first().humidity == testForecast.upcomingDays.first().humidity)
      assert(testField.listForecast.first().upcomingDays.first().windSpeed == testForecast.upcomingDays.first().windSpeed)
      assert(testField.listForecast.first().upcomingDays.first().windDir == testForecast.upcomingDays.first().windDir)
      assert(testField.listForecast.first().upcomingDays.first().pressure == testForecast.upcomingDays.first().pressure)
      assert(testField.listForecast.first().upcomingDays.first().uvIndex == testForecast.upcomingDays.first().uvIndex)
      assert(testField.listForecast.first().upcomingDays.first().cloudCover == testForecast.upcomingDays.first().cloudCover)
      assert(testField.listForecast.first().upcomingDays.first().precipProb == testForecast.upcomingDays.first().precipProb)
      assert(testField.listForecast.first().upcomingDays.first().precip == testForecast.upcomingDays.first().precip)
      assert(testField.listForecast.first().upcomingDays.first().precipType == testForecast.upcomingDays.first().precipType)
      assert(testField.listForecast.first().upcomingDays.first().description == testForecast.upcomingDays.first().description)
      assert(testField.listForecast.first().upcomingDays.first().icon == testForecast.upcomingDays.first().icon)
      assert(testField.listForecast.first().upcomingDays.first().sunrise == testForecast.upcomingDays.first().sunrise)
      assert(testField.listForecast.first().upcomingDays.first().sunset == testForecast.upcomingDays.first().sunset)
      assert(testField.listForecast.first().upcomingDays.first().moonrise == testForecast.upcomingDays.first().moonrise)
      assert(testField.listForecast.first().upcomingDays.first().moonset == testForecast.upcomingDays.first().moonset)
      assert(testField.listForecast.first().upcomingDays.first().moonPhase == testForecast.upcomingDays.first().moonPhase)
      assert(testField.listForecast.first().upcomingHours.first().date == testForecast.upcomingHours.first().date)
      assert(testField.listForecast.first().upcomingHours.first().temp == testForecast.upcomingHours.first().temp)
      assert(testField.listForecast.first().upcomingHours.first().icon == testForecast.upcomingHours.first().icon)
      assert(testField.listForecast.first().upcomingHours.first().pressure == testForecast.upcomingHours.first().pressure)
      assert(testField.listForecast.first().upcomingHours.first().humidity == testForecast.upcomingHours.first().humidity)
      assert(testField.listForecast.first().upcomingHours.first().uvIndex == testForecast.upcomingHours.first().uvIndex)
      assert(testField.listForecast.first().upcomingHours.first().precipProb == testForecast.upcomingHours.first().precipProb)
      assert(testField.listForecast.first().upcomingHours.first().precipType == testForecast.upcomingHours.first().precipType)
    }

  @Test
  fun `check that when a class is created, state of dropdown menu is initial`() = runTest {
    // Arrange
    mockSuccessResult()
    // Act
    val store = SUT.create()
//    store.accept(FavouriteStore.Intent.ClosingActionMenu)
    val testField = store.state.dropDownMenuState
    // Assert
    assert(testField == FavouriteStore.State.DropDownMenuState.Initial)
  }

  @Test
  fun `check that when the menu button is pressed the state of the drop-down menu is open`() =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      store.accept(FavouriteStore.Intent.ActionMenuClicked)
      val testField = store.state.dropDownMenuState
      // Assert
      assert(testField == FavouriteStore.State.DropDownMenuState.OpenMenu)
    }

  @Test
  fun `check that when the menu button is pressed the state of the drop-down menu is closed`() =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      store.accept(FavouriteStore.Intent.ClosingActionMenu)
      val testField = store.state.dropDownMenuState
      // Assert
      assert(testField == FavouriteStore.State.DropDownMenuState.CloseMenu)
    }

  @Test
  fun `check that when the search menu button is pressed the label status appears`() = runTest {
    // Arrange
    mockSuccessResult()
    // Act
    val store = SUT.create()
    val testField = store.labels.test()
    store.accept(FavouriteStore.Intent.SearchClicked)
    // Assert
    assert(testField == listOf(FavouriteStore.Label.OnSearchClicked))
  }

  @Test
  fun `check that when the search menu button is pressed the state of the search menu is open`() =
    runTest {
      // Arrange
      mockSuccessResult()

      // Act
      val store = SUT.create()
      val testField = store.labels.test()
      store.accept(FavouriteStore.Intent.ItemMenuSettingsClicked)
      // Assert
      assert(testField == listOf(FavouriteStore.Label.OnItemMenuSettingsClicked))
    }

  @Test
  fun `check that when the search menu button is pressed the state of the search menu is closed`() =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      val testField = store.labels.test()
      store.accept(FavouriteStore.Intent.ItemCityClicked(testCityFs.id))
      // Assert
      assert(testField == listOf(FavouriteStore.Label.OnItemClicked(testCityFs.id, listForecast.mapToForecastScreenList())))
    }

  // region helper functions

  private suspend fun mockSuccessResult() {
    whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(getForecastUseCase.invoke(cities)).thenReturn(Result.success(listForecast))
  }

  private fun CityFs.toTestCityForTest() = City(
    id = id,
    name = name,
    region = region,
    country = country,
    lat = lat,
    lon = lon
  )
  // endregion helper functions

}