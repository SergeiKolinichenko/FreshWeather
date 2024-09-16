package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.repositories.ForecastRepository
import info.sergeikolinichenko.domain.usecases.forecast.GetForecastsFromNetUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 15.06.2024 at 18:30 (GMT+3) **/

class getForecastsFromNetUseCaseShould {

  // region constants
  private val repository = mock<ForecastRepository>()
  private val forecasts = mock<List<Forecast>>()
  private val cities = mock<List<City>>()
  private val exception = Exception("Something went wrong")
  // endregion constants

  // system under test
  private val SUT = GetForecastsFromNetUseCase(repository)

  @Test
  fun `get forecast from repository`(): Unit = runTest {
    // Act
    SUT.invoke(cities)
    // Assert
    verify(repository, times(1)).getForecastsFromNet(cities)
  }

  @Test
  fun `return forecast from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getForecastsFromNet(cities)).thenReturn(Result.success(forecasts))
    // Act
    val result = SUT.invoke(cities).getOrNull()
    // Assert
    assertEquals(result, forecasts)
  }

  @Test
  fun `return error from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getForecastsFromNet(cities)).thenReturn(Result.failure(exception))
    // Act
    val result = SUT.invoke(cities).exceptionOrNull()?.message
    // Assert
    assertEquals(result, exception.message)
  }
}

