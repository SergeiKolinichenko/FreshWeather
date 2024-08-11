package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import info.sergeikolinichenko.domain.usecases.weather.GetWeatherUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 16.06.2024 at 13:47 (GMT+3) **/

class GetWeatherUseCaseShould {
  // region constants
  private val repository = mock<WeatherRepository>()
  private val weather = mock<Weather>()
  private val city = mock<City>()
  private val exception = Exception("Something went wrong")
  // endregion  constants

  // system under test
  private val SUT = GetWeatherUseCase(repository)

  @Test
  fun `get weather from repository`(): Unit = runTest {
    // Act
    SUT.invoke(city)
    // Assert
    verify(repository, times(1)).getWeather(city)
  }

  @Test
  fun `return weather from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getWeather(city)).thenReturn(Result.success(weather))
    // Act
    val result = SUT.invoke(city).getOrNull()
    // Assert
    assert(result == weather)
  }

  @Test
  fun `return error from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getWeather(city)).thenReturn(Result.failure(exception))
    // Act
    val result = SUT.invoke(city).exceptionOrNull()?.message
    // Assert
    assert(result == exception.message)
  }

}