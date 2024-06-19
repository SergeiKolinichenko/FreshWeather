package info.sergeikolinichenko.myapplication.usecases

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.CurrentWeather
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import info.sergeikolinichenko.domain.usecases.GetWeatherUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

/** Created by Sergei Kolinichenko on 16.06.2024 at 13:47 (GMT+3) **/

class GetWeatherUseCaseShould {
  // region constants
  private val repository = mock<WeatherRepository>()
  private val currentWeather = mock<CurrentWeather>()
  private val id = 1
  private val SUT = GetWeatherUseCase(repository)
  // endregion  constants
  @Test
  fun `get weather from repository`(): Unit = runBlocking {
    // Act
    SUT.invoke(id)
    // Assert
    verify(repository, times(1)).getWeather(id)
  }
  @Test
  fun `return weather from repository`(): Unit = runBlocking {
    // Arrange
    whenever(repository.getWeather(id)).thenReturn(currentWeather)
    // Act
    val result = SUT.invoke(id)
    // Assert
    assert(result == currentWeather)
  }
}