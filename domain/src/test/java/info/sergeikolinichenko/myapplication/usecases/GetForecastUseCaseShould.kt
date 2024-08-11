package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import info.sergeikolinichenko.domain.usecases.weather.GetForecastUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 15.06.2024 at 18:30 (GMT+3) **/

class GetForecastUseCaseShould {

  // region constants
  private val repository = mock<WeatherRepository>()
  private val forecast = mock<Forecast>()
  private val city = mock<City>()
  private val exception = Exception("Something went wrong")
  // endregion constants

  // system under test
  private val SUT = GetForecastUseCase(repository)

  @Test
  fun `get forecast from repository`(): Unit = runTest {
    // Act
    SUT.invoke(city)
    // Assert
    verify(repository, times(1)).getForecast(city)
  }

  @Test
  fun `return forecast from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getForecast(city)).thenReturn(Result.success(forecast))
    // Act
    val result = SUT.invoke(city).getOrNull()
    // Assert
    assertEquals(result, forecast)
  }

  @Test
  fun `return error from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getForecast(city)).thenReturn(Result.failure(exception))
    // Act
    val result = SUT.invoke(city).exceptionOrNull()?.message
    // Assert
    assertEquals(result, exception.message)
  }
}

