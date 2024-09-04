package info.sergeikolinichenko.myapplication.store

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.weather.GetForecastUseCase
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStoreFactory
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreen
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.testForecast
import info.sergeikolinichenko.myapplication.utils.toCityScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 31.08.2024 at 20:49 (GMT+3) **/

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsStoreFactoryShould : BaseUnitTestsRules() {

  // region constants
  private val factory = DefaultStoreFactory()
  private val getForecastUseCase = mock<GetForecastUseCase>()
  private val getFavouriteCitiesUseCase = mock<GetFavouriteCitiesUseCase>()

  private val cities = listOf(testCity)
  // endregion constants

  private val SUT = DetailsStoreFactory(
    factory,
    getForecastUseCase,
    getFavouriteCitiesUseCase
  )

  @Test
  fun `call verification of getForecastUseCase`() = runTest {
    // Arrange
    mockSuccessfulResult()
    // Act
    SUT.create(testCity.id)
    // Assert
    verify(getFavouriteCitiesUseCase, times(1)).invoke()
  }

  @Test
  fun `call verification of getFavouriteCitiesUseCase`() = runTest {
    // Arrange
    mockSuccessfulResult()
    val cities = listOf(testCity)
    // Act
    val store = SUT.create(testCity.id)
    val testField = store.state.citiesState
    // Assert
    if (testField is DetailsStore.State.CitiesState.Loaded) {
      assert(testField.id == testCity.id)
      assert(testField.cities.first() == cities.first().toCityScreen())
    } else {
      assert(false)
    }
  }

  @Test
  fun `check that when a class is created, the favourite cities are not loaded from Network`() =
    runTest {
      // Arrange
      whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.failure(RuntimeException("Something went wrong"))))
      // Act
      val store = SUT.create(testCity.id)
      // Assert
      assert(store.state.citiesState is DetailsStore.State.CitiesState.Error)
    }


  @Test
  fun `check that when a class is created, the forecast is loaded from Network`() = runTest {
    // Arrange
    mockSuccessfulResult()
    // Act
    val store = SUT.create(testCity.id)
    val testField = store.state.forecastState
    // Assert
    if (testField is DetailsStore.State.ForecastState.Loaded) {
      assert(testField.forecast == testForecast.mapToForecastScreen())
    } else {
      assert(false)
    }
  }

  @Test
  fun `check that when a class is created, the forecast is not loaded from Network`() = runTest {
    // Arrange
    whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(getForecastUseCase.invoke(testCity)).thenReturn(Result.failure(Exception("Something went wrong")))
    // Act
    val store = SUT.create(testCity.id)
    // Assert
    assert(store.state.forecastState is DetailsStore.State.ForecastState.Error)
  }



  private suspend fun mockSuccessfulResult() {
    whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(getForecastUseCase.invoke(testCity)).thenReturn(Result.success(testForecast))
  }

}