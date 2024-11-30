package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.repositories.ForecastRepository
import info.sergeikolinichenko.domain.usecases.forecast.HandleForecastInDbUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/** Created by Sergei Kolinichenko on 24.11.2024 at 19:44 (GMT+3) **/

class HandleForecastInDbUseCaseShould {
  // region helper fields
  private val mockRepository = mock<ForecastRepository>()
  private val mockListForecast = mock<List<Forecast>>()
  private val exception = RuntimeException("Something went wrong")
  // endregion helper fields

  lateinit var SUT: HandleForecastInDbUseCase

  @Before
  fun setup() {
    SUT = HandleForecastInDbUseCase(mockRepository)
  }

  @Test
  fun `check if the function for setting forecast to db has been called up`() = runTest {
    // Act
    SUT.insertForecastToDb(mockListForecast)
    // Assert
    verify(mockRepository, Mockito.times(1)).insertForecastsToDb(mockListForecast)
  }

  @Test
  fun `check if the function for getting forecast from db has been called up`() {
    // Act
    SUT.getForecastsFromDb()
    // Assert
    verify(mockRepository, Mockito.times(1)).getForecastsFromDb
  }

  @Test
  fun `verify get forecast from db successfully`() = runTest {
    // Arrange
    Mockito.`when`(mockRepository.getForecastsFromDb).thenReturn(
      flow { emit(Result.success(mockListForecast)) }
    )
    // Act
    val result = SUT.getForecastsFromDb().first()
    // Assert
    assert(mockListForecast == result.getOrNull())
  }
  
  @Test
  fun `verify get forecast from db error`() = runTest {
    // Arrange
    Mockito.`when`(mockRepository.getForecastsFromDb).thenReturn(
      flow { emit(Result.failure(exception)) }
    )
    // Act
    val result = SUT.getForecastsFromDb().first()
    // Assert
    assert(exception.message == result.exceptionOrNull()?.message)
  }

}