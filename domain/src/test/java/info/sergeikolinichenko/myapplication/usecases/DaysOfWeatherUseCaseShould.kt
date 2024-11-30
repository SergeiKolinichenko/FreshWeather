package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.repositories.SettingsRepository
import info.sergeikolinichenko.domain.usecases.settings.DaysOfWeatherUseCase
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DaysOfWeatherUseCaseShould {
  // region helper fields
  private val mockRepository = mock<SettingsRepository>()
  private val mockDays = 5
  // endregion helper fields

  lateinit var SUT: DaysOfWeatherUseCase

  @Before
  fun setup() {
    SUT = DaysOfWeatherUseCase(mockRepository)
  }

  @Test
  fun `check if the function for saving weather days has been called up` () {
    // Act
    SUT.setDaysOfWeather(mockDays)
    // Assert
    verify(mockRepository, Mockito.times(1)).setDaysOfWeather(mockDays)
  }

  @Test
  fun `check if the function for retrieving weather days has been called up` () {
    // Act
    SUT.getDaysOfWeather()
    // Assert
    verify(mockRepository, Mockito.times(1)).getDaysOfWeather()
  }

  @Test
  fun `verify getting default days of weather` () {
    // Arrange
    Mockito.`when`(mockRepository.getDaysOfWeather()).thenReturn(7)
    // Act
    val result = SUT.getDaysOfWeather()
    // Assert
    assert(result == 7)
  }

  @Test
  fun `verify getting another days of weather` () {
    // Arrange
    Mockito.`when`(mockRepository.getDaysOfWeather()).thenReturn(mockDays)
    // Act
    val result = SUT.getDaysOfWeather()
    // Assert
    assert(result == mockDays)
  }

}