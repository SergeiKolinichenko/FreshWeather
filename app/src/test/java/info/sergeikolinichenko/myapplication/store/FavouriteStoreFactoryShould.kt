package info.sergeikolinichenko.myapplication.store

import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesFromDbUseCase
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastsFromNetUseCase
import info.sergeikolinichenko.domain.usecases.forecast.HandleForecastInDbUseCase
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import info.sergeikolinichenko.myapplication.entity.CityFs
import info.sergeikolinichenko.myapplication.presentation.stores.favourites.FavouriteStore
import info.sergeikolinichenko.myapplication.presentation.stores.favourites.FavouriteStoreFactory
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.DoNeedNewOne
import info.sergeikolinichenko.myapplication.utils.test
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.testCityFs
import info.sergeikolinichenko.myapplication.utils.testForecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
  private val getForecastsFromNetUseCase = mock<GetForecastsFromNetUseCase>()
  private val getFavouriteCitiesFromDbUseCase = mock<GetFavouriteCitiesFromDbUseCase>()
  private val changeFavouriteStateUseCase = mock<ChangeFavouriteStateUseCase>()
  private val searchCitiesUseCase = mock<SearchCitiesUseCase>()
  private val handleForecastIntoDb = mock<HandleForecastInDbUseCase>()
  private val doNeedNewOne = mock<DoNeedNewOne>()

  private val cities = listOf(testCity)
  private val listForecast = listOf(testForecast)
  private val exception = Exception("Something went wrong")
  // endregion constants

  private val systemUnderTest = FavouriteStoreFactory(
    factory,
    getFavouriteCitiesFromDbUseCase,
    changeFavouriteStateUseCase,
    searchCitiesUseCase,
    getForecastsFromNetUseCase,
    handleForecastIntoDb,
    doNeedNewOne
  )

  @Test
  fun `check that when the class is created, it is executed by calling GetFavouriteCitiesUseCase`() =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      systemUnderTest.create()
      // Assert
      verify(getFavouriteCitiesFromDbUseCase, times(1)).invoke()
    }

  @Test
  fun `check that when a class is created, the favourite cities are loaded from DB`(): Unit =
    runTest {
      // Arrange
      mockSuccessResult()
      // Act
      val store = systemUnderTest.create()
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
      val store = systemUnderTest.create()
      val testField = store.state.citiesState
      // Assert
      assert(testField is FavouriteStore.State.CitiesState.Loaded)
    }

  @Test
  fun `check that when a class is created, the favourite cities are not loaded from DB cities state is error`(): Unit =
    runTest {
      // Arrange
      whenever(getFavouriteCitiesFromDbUseCase.invoke()).thenReturn(flowOf(Result.failure(exception)))
      // Act
      val store = systemUnderTest.create()
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
      systemUnderTest.create()
      // Assert
      verify(getForecastsFromNetUseCase, times(1)).invoke(cities)
    }

  @Test
  fun `check that when a class is created, the favourite cities weather are not loaded from Network`(): Unit =
    runTest {
      // Arrange
      whenever(getFavouriteCitiesFromDbUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
      whenever(getForecastsFromNetUseCase.invoke(cities)).thenReturn(Result.failure(exception))
      whenever(handleForecastIntoDb.getForecastsFromDb()).thenReturn(
        flow { emit(Result.failure(Exception()) )}
      )
      // Act
      val store = systemUnderTest.create()
      val testField = store.state.forecastState
      // Assert
      assert(testField is FavouriteStore.State.ForecastState.Error)
    }

  @Test
  fun `check that when a class is created, the favourite cities weather are loaded from Network`(): Unit =
    runTest {
      // Arrange
      whenever(getFavouriteCitiesFromDbUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
      whenever(getForecastsFromNetUseCase.invoke(cities)).thenReturn(Result.success(listForecast))
      whenever(handleForecastIntoDb.getForecastsFromDb()).thenReturn(
        flow { emit(Result.failure(Exception()))}
      )
      // Act
      systemUnderTest.create()
      // Assert
      verify(getForecastsFromNetUseCase, times(1)).invoke(cities)
    }

  @Test
  fun `check that when a class is created, state of dropdown menu is initial`() = runTest {
    // Arrange
    mockSuccessResult()
    // Act
    val store = systemUnderTest.create()
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
      val store = systemUnderTest.create()
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
      val store = systemUnderTest.create()
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
    val store = systemUnderTest.create()
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
      val store = systemUnderTest.create()
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
      val store = systemUnderTest.create()
      val testField = store.labels.test()
      store.accept(FavouriteStore.Intent.ItemCityClicked(testCityFs.id))
      // Assert
      assert(testField == listOf(FavouriteStore.Label.OnItemClicked(testCityFs.id)))
    }

  // region helper functions

  private suspend fun mockSuccessResult() {
    whenever(getFavouriteCitiesFromDbUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(getForecastsFromNetUseCase.invoke(cities)).thenReturn(Result.success(listForecast))
    whenever(handleForecastIntoDb.getForecastsFromDb()).thenReturn(
      flow { emit(Result.failure(Exception()) )}
    )
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