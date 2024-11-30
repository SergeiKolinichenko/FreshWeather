package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.repositories.SearchRepository
import info.sergeikolinichenko.domain.usecases.search.SearchCitiesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

/** Created by Sergei Kolinichenko on 16.06.2024 at 17:59 (GMT+3) **/

class SearchCitiesUseCaseShould {
  private lateinit var repository: SearchRepository
  private lateinit var useCase: SearchCitiesUseCase

  @Before
  fun setup() {
    repository = mock()
    useCase = SearchCitiesUseCase(repository)
  }

  @Test
  fun `invoke calls repository and returns result`() = runTest {
    val query = "London"
    val expectedResult = listOf<Any>()

//    whenever(repository.searchCities<Any>(query)).thenReturn(expectedResult)

//    val result = useCase<Any>(query)

//    verify(repository, times(1)).searchCities<Any>(query)
//    assert(result == expectedResult)
  }

  @Test(expected = Exception::class)
  fun `invoke throws exception when repository throws exception`() = runTest {
    val query = "Paris"

//    whenever(repository.searchCities<Any>(query)).thenThrow(Exception("Error")) // Replace Any

//    val result = useCase<Any>(query) // Replace Any

//    verify(repository, times(1)).searchCities<Any>(query)
//    Assert.assertNull(result)

  }
}