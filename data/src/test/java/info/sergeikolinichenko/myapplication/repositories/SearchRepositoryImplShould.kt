package info.sergeikolinichenko.myapplication.repositories

import info.sergeikolinichenko.myapplication.mappers.toListCities
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response


/** Created by Sergei Kolinichenko on 19.06.2024 at 14:38 (GMT+3) **/

class SearchRepositoryImplShould {
  private lateinit var repository: SearchRepositoryImpl
  private lateinit var mockApiService: ApiService
  private lateinit var mockApiFactory: ApiFactory

  @Before
  fun setup() {
    mockApiService = mock()
    mockApiFactory = mock {
      on { getWeatherapiApi() } doReturn mockApiService}
    repository = SearchRepositoryImpl(mockApiFactory)
  }

  @Test
  fun `searchCities returns a list of cities when response is successful`() = runBlocking {
    // Arrange
    val query = "London"
    val cityDto1 = CityDto(
      idCity = 1,
      nameCity = "London",
      regionCity = "England",
      countryCity = "United Kingdom",
      lat = 51.5074,
      lon = 0.1278,
    )
    val cityDto2 = CityDto(
      idCity = 2,
      nameCity = "Londonderry",
      regionCity = "Northern Ireland",
      countryCity = "United Kingdom",
      lat = 55.0074,
      lon = -7.3078
    )
    val cityListDto = listOf(cityDto1, cityDto2)
    val expectedCityList = cityListDto.toListCities()
    val response= Response.success(cityListDto)

    doReturn(response).`when`(mockApiService).searchCities(query)

    // Act
    val result = repository.searchCities(query)

    // Assert
    assertEquals(expectedCityList, result)
  }

  @Test
  fun `searchCities throws exception when response is not successful`() = runBlocking {
    // Arrange
    val query = "London"
    val response = Response.error<List<CityDto>>(404, mock())

    doReturn(response).`when`(mockApiService).searchCities(query)

    // Act & Assert
    try {
      repository.searchCities(query)
      assert(false) // Should not reach this line
    } catch (e: Exception) {
      assertEquals("Error while searching cities", e.message)
    }
  }

}