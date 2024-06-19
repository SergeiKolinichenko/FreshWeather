package info.sergeikolinichenko.myapplication.usecases

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.usecases.GetFavouriteCitiesUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/** Created by Sergei Kolinichenko on 15.06.2024 at 17:28 (GMT+3) **/

class GetFavouriteCitiesUseCaseShould {

  // region constants
  private val repository = mock<FavouriteRepository>()
  private val listCities = mock<List<City>>()
  private val SUT = GetFavouriteCitiesUseCase(repository)
  // endregion  constants
  @Test
  fun `get list of favourite cities from repository`(): Unit = runBlocking {
    // Act
    SUT.invoke()
    // Assert
    verify(repository, times(1)).getFavouriteCities
  }
  @Test
  fun `return list of favourite cities from repository`() = runBlocking {
    // Arrange
    mockGetListCities()
    // Act
    val result = SUT.invoke().first()
    // Assert
    assertEquals(listCities, result)
  }
  // region helper functions
  private fun mockGetListCities() {
    whenever(repository.getFavouriteCities).thenReturn(
      flow {
        emit(listCities)
      }
    )
  }
  // endregion helper functions
}