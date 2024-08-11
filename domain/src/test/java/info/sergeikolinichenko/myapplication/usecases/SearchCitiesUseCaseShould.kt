package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.SearchRepository
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 16.06.2024 at 17:59 (GMT+3) **/

class SearchCitiesUseCaseShould {
  // region constants
  private val repository = mock<SearchRepository>()
  private val query = "query"
  private val listCities = mock<List<City>>()

  // endregion constants
  private val SUT = SearchCitiesUseCase(repository)

  @Test
  fun `get list of cities`(): Unit = runTest {
    // Act
    SUT.invoke(query)
    // Assert
    verify(repository, times(1)).searchCities(query)
  }

  @Test
  fun `return list of cities`(): Unit = runTest {
    // Arrange
    whenever(repository.searchCities(query)).thenReturn(listCities)
    // Act
    val result = SUT.invoke(query)
    // Assert
    assert(result == listCities)
  }
}