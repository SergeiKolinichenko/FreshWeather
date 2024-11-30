package info.sergeikolinichenko.myapplication.store

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastsFromNetUseCase
import info.sergeikolinichenko.domain.usecases.forecast.HandleForecastInDbUseCase
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStoreFactory
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.DoNeedNewOne
import info.sergeikolinichenko.myapplication.utils.mapCityToCityFs
import info.sergeikolinichenko.myapplication.utils.mapToForecastScreen
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.testForecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 31.08.2024 at 20:49 (GMT+3) **/

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsStoreFactoryShould : BaseUnitTestsRules() {

  // region constants
  private val factory = DefaultStoreFactory()
  private val getForecastsFromNetUseCase = mock<GetForecastsFromNetUseCase>()
  private val getFavouriteCitiesUseCase = mock<GetFavouriteCitiesUseCase>()
  private val handleForecastIntoDb = mock<HandleForecastInDbUseCase>()
  private val doNeedNewOne = mock<DoNeedNewOne>()

  private val cities = listOf(testCity)
  private lateinit var listOfForecastFs: List<ForecastFs>
  private val listOfForecast = listOf(testForecast)
  // endregion constants

  private val systemUnderTest = DetailsStoreFactory(
    factory,
    getForecastsFromNetUseCase,
    getFavouriteCitiesUseCase,
    handleForecastIntoDb,
    doNeedNewOne
  )

  @Before
 fun setUp() {
    listOfForecastFs = listOfForecast.map { it.mapToForecastScreen() }
  }

  @Test
  fun `call verification of getForecastUseCase`() = runTest {
    // Arrange
    mockSuccessfulResult()
    // Act
    systemUnderTest.create(testCity.id)
    // Assert
    verify(getFavouriteCitiesUseCase, times(1)).invoke()
  }

  @Test
  fun `call verification of getFavouriteCitiesUseCase`() = runTest {
    // Arrange
    mockSuccessfulResult()
    // Act
    val store = systemUnderTest.create(testCity.id)
    val testField = store.state.citiesState
    // Assert
    if (testField is DetailsStore.State.CitiesState.Loaded) {
      assert(testField.id == testCity.id)
      assert(testField.cities.first() == cities.first().mapCityToCityFs())
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
      val store = systemUnderTest.create(testCity.id)
      // Assert
      assert(store.state.citiesState is DetailsStore.State.CitiesState.LoadingFailed)
    }


  @Test
  fun `check that when a class is created, the forecast is loaded from Network`() = runTest {
    // Arrange
//    mockSuccessfulResult()
    whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(handleForecastIntoDb.getForecastsFromDb()).thenReturn(
      flow { emit(Result.success(listOfForecast)) }
    )
    whenever(doNeedNewOne.invoke(any(), any())).thenReturn(true)
    whenever(getForecastsFromNetUseCase.invoke(cities)).thenReturn(Result.success(listOfForecast))
    whenever(handleForecastIntoDb.insertForecastToDb(listOfForecast)).then {

    }
    // Act
    val store = systemUnderTest.create(testCity.id)
    val testField = store.state
    // Assert
    assert(testField.citiesState is DetailsStore.State.CitiesState.Loaded)
    println("state ${testField.forecastState}")
//    assert(testField.forecastState is DetailsStore.State.ForecastsState.Loaded)
//    assert((testField.forecastState as DetailsStore.State.ForecastsState.Loaded).forecasts.first() == listOfForecastFs.first())
  }

  @Test
  fun `check that when a class is created, the forecast is not loaded from Network`() = runTest {
    // Arrange
    whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
    whenever(getForecastsFromNetUseCase.invoke(cities)).thenReturn(Result.failure(Exception("Something went wrong")))
    // Act
    val store = systemUnderTest.create(testCity.id)
    // Assert
    println("state ${store.state.citiesState}")
//    assert(store.state.citiesState is DetailsStore.State.CitiesState.LoadingFailed)
  }



  private suspend fun mockSuccessfulResult() {
//    whenever(getFavouriteCitiesUseCase.invoke()).thenReturn(flowOf(Result.success(cities)))
//    whenever(getForecastsFromNetUseCase.invoke(cities)).thenReturn(Result.success(listOfForecast))
    whenever(handleForecastIntoDb.getForecastsFromDb()).thenReturn(
      flow { Result.success(listOfForecast) }
    )
  }

}