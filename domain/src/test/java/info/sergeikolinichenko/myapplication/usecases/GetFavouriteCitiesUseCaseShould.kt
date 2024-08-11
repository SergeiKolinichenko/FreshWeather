package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.usecases.favourite.GetFavouriteCitiesUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 15.06.2024 at 17:28 (GMT+3) **/

class GetFavouriteCitiesUseCaseShould {

  // region constants
  private val repository = mock<FavouriteRepository>()
  private val listCities = mock<List<City>>()
  private val exception = RuntimeException("Something went wrong")
  // endregion  constants

  private val SUT = GetFavouriteCitiesUseCase(repository)

  @Test
  fun `get list of favourite cities from repository`(): Unit = runTest {
    // Act
    SUT.invoke()
    // Assert
    verify(repository, times(1)).getFavouriteCities
  }
  @Test
  fun `return list of favourite cities from repository successfully`() = runTest {
    // Arrange
    mockGetSuccessfullyListOfCities()
    // Act
    val result = SUT.invoke().first()
    // Assert
    assertEquals(listCities, result.getOrNull())
  }
  @Test
  fun `returns an error from the repository`() = runTest {
    // Arrange
    whenever(repository.getFavouriteCities).thenReturn(
      flow {
        emit(Result.failure(exception))
      })
    val result = SUT.invoke().first()
    // Assert
    assertEquals(exception, result.exceptionOrNull())
  }
  // region helper functions
  private fun mockGetSuccessfullyListOfCities() {
    whenever(repository.getFavouriteCities).thenReturn(
      flow {
        emit(Result.success(listCities))
      }
    )
  }
  // endregion helper functions
}