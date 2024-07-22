package info.sergeikolinichenko.myapplication.usecases

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.domain.repositories.WeatherRepository
import info.sergeikolinichenko.domain.usecases.weather.GetWeatherUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** Created by Sergei Kolinichenko on 16.06.2024 at 13:47 (GMT+3) **/

class GetWeatherUseCaseShould {
  // region constants
  private val repository = mock<WeatherRepository>()
  private val weather = mock<Weather>()
  private val id = 1
  private val exception = Exception("Something went wrong")
  // endregion  constants

  // system under test
  private val SUT = GetWeatherUseCase(repository)

  @Test
  fun `get weather from repository`(): Unit = runTest {
    // Act
    SUT.invoke(id)
    // Assert
    verify(repository, times(1)).getWeather(id)
  }

  @Test
  fun `return weather from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getWeather(id)).thenReturn(Result.success(weather))
    // Act
    val result = SUT.invoke(id).getOrNull()
    // Assert
    assert(result == weather)
  }

  @Test
  fun `return error from repository`(): Unit = runTest {
    // Arrange
    whenever(repository.getWeather(id)).thenReturn(Result.failure(exception))
    // Act
    val result = SUT.invoke(id).exceptionOrNull()?.message
    // Assert
    assert(result == exception.message)
  }

}