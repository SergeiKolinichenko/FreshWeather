package info.sergeikolinichenko.myapplication.store

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetWeatherUseCase
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStoreFactory
import info.sergeikolinichenko.myapplication.utils.test
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.testCityForScreen
import info.sergeikolinichenko.myapplication.utils.testWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** Created by Sergei Kolinichenko on 02.07.2024 at 17:05 (GMT+3) **/

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteStoreFactoryShould : BaseUnitTestsRules() {

  // region constants
  private val factory = DefaultStoreFactory()
  private val getFavouriteCities = mock<GetFavouriteCitiesUseCase>()
  private val getWeatherUseCase = mock<GetWeatherUseCase>()
  private val cities = listOf(testCity)
  private val exception = Exception("Something went wrong")
  // endregion constants

  private val SUT = FavouriteStoreFactory(
    factory,
    getFavouriteCities,
    getWeatherUseCase
  )

  @Test
  fun `check that when the class is created, it is executed by calling GetFavouriteCitiesUseCase`(): Unit = runTest {
    // Arrange
    mockSuccessResult()
    // Act
    SUT.create()
    // Assert
    verify(getFavouriteCities, times(1)).invoke()
  }

  @Test
  fun `check that when a class is created, the favourite cities are loaded from DB`(): Unit = runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      val testField = store.state.cityItems.first().city.toTestCityForTest()
      // Assert
      assert(testField == testCity)
    }

  @Test
  fun `check that when a class is created, the favourite cities loaded and is state is loaded`(): Unit = runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = SUT.create()
      val testField = store.state.listCitiesLoadedState
      // Assert
      assert(testField == FavouriteStore.State.ListCitiesLoadedState.Loaded)
    }

  @Test
  fun `check that when a class is created, the favourite cities are not loaded from DB cities state is error`(): Unit = runTest {
      // Arrange
      whenever(getFavouriteCities.invoke()).thenReturn(flowOf(Result.failure(exception)))
      // Act
      val store = SUT.create()
      val testField = store.state.listCitiesLoadedState
      // Assert
      assert(testField == FavouriteStore.State.ListCitiesLoadedState.Error)
    }

  @Test
  fun `check that when the class is created, it is executed by calling GetWeatherUseCase`(): Unit = runTest {
    // Arrange
    mockSuccessResult()
    // Act
    SUT.create()
    // Assert
    verify(getWeatherUseCase, times(1)).invoke(testCity.id)
  }

  @Test
  fun `check that when a class is created, the favourite cities weather are not loaded from Network`(): Unit = runTest {
    // Arrange
    whenever(getFavouriteCities.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(getWeatherUseCase.invoke(testCity.id)).thenReturn(Result.failure(exception))
    // Act
    val store = SUT.create()
    val testField = store.state.cityItems.first().weatherLoadingState
    // Assert
    assert(testField == FavouriteStore.State.WeatherLoadingState.Error)
  }

  @Test
  fun `check that when a class is created, the favourite cities weather are loaded from Network`(): Unit = runTest {
    // Arrange
    mockSuccessResult()
    // Act
    val store = SUT.create()
    val testField =
      store.state.cityItems.first().weatherLoadingState
          as FavouriteStore.State.WeatherLoadingState.LoadedWeatherLoading
    // Assert
    assert(testField.temp == testWeather.temp)
    assert(testField.maxTemp == testWeather.maxTemp)
    assert(testField.minTemp == testWeather.minTemp)
    assert(testField.description == testWeather.description)
    assert(testField.iconUrl == testWeather.condIconUrl)
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
  fun `check that when the menu button is pressed the state of the drop-down menu is open`() = runTest {
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
  fun `check that when the menu button is pressed the state of the drop-down menu is closed`() = runTest {
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
    assert(testField == listOf(FavouriteStore.Label.OnClickSearch))
  }

  @Test
  fun `check that when the search menu button is pressed the state of the search menu is open`() = runTest {
    // Arrange
    mockSuccessResult()

    // Act
    val store = SUT.create()
    val testField = store.labels.test()
    store.accept(FavouriteStore.Intent.ItemMenuSettingsClicked)

    // Assert
    assert(testField == listOf(FavouriteStore.Label.OnClickItemMenuSettings))
  }

  @Test
  fun `check that when the search menu button is pressed the state of the search menu is closed`() = runTest {
    // Arrange
    mockSuccessResult()
    // Act
    val store = SUT.create()
    val testField = store.labels.test()
    store.accept(FavouriteStore.Intent.ItemCityClicked(testCityForScreen, 111))
    // Assert
    assert(testField == listOf(FavouriteStore.Label.OnClickCity(testCityForScreen, 111)))
  }

  // region helper functions

  private suspend fun mockSuccessResult() {
    whenever(getFavouriteCities.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(getWeatherUseCase.invoke(testCity.id)).thenReturn(Result.success(testWeather))
  }

  private fun CityForScreen.toTestCityForTest() = City(
    id = id,
    name = name,
    region = region,
    country = country
  )
  // endregion helper functions

}