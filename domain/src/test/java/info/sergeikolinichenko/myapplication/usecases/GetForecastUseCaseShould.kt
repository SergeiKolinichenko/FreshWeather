package info.sergeikolinichenko.myapplication.usecases

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import info.sergeikolinichenko.domain.usecases.GetForecastUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/** Created by Sergei Kolinichenko on 15.06.2024 at 18:30 (GMT+3) **/

class GetForecastUseCaseShould {

  // region constants
  private val repository = mock<WeatherRepository>()
  private val forecast = mock<Forecast>()
  private val id = 1
  private val SUT = GetForecastUseCase(repository)
  // endregion constants
  @Test
  fun `get forecast from repository`(): Unit = runBlocking {
    // Act
    SUT.invoke(id)
    // Assert
    verify(repository, times(1)).getForecast(id)
  }
  @Test
  fun `return forecast from repository`(): Unit = runBlocking {
    // Arrange
    whenever(repository.getForecast(id)).thenReturn(forecast)
    // Act
    val result = SUT.invoke(id)
    // Assert
    assertEquals(forecast, result)
  }
}