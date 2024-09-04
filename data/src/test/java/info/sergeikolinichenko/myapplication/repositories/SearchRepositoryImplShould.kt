package info.sergeikolinichenko.myapplication.repositories

import info.sergeikolinichenko.myapplication.mappers.toCity
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import retrofit2.Response


/** Created by Sergei Kolinichenko on 19.06.2024 at 14:38 (GMT+3) **/

class SearchRepositoryImplShould {

  @Mock private lateinit var mockApiService: ApiService
  @Mock private lateinit var mockResponse: Response<List<CityDto>> // Use a generic type for the response body
  @Mock private lateinit var mockRepository: SearchRepositoryImpl

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    mockApiService = mock()
    mockRepository = SearchRepositoryImpl()
    ApiFactory.apiServiceForWeatherapi = mockApiService // Inject the mocked API
  }

  @Test
  fun `searchCities returns cities on successful response`() = runBlocking {

    // Mock a successful response
    Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
    Mockito.`when`(mockResponse.body()).thenReturn(listOf(cityDto1, cityDto2)) // Mock response body
    Mockito.`when`(mockApiService.searchCities(Mockito.anyString())).thenReturn(mockResponse)

    // Call the function and assert the result
    val cities = mockRepository.searchCities(query)
    assert(cities.isNotEmpty()) // Or more specific assertions based on your toListCities() logic
    assertEquals(2, cities.size)
    assertEquals(cityDto1.toCity(), cities[0])
    assertEquals(cityDto2.toCity(), cities[1])
  }

  @Test(expected = Exception::class)
  fun `searchCities throws exception on unsuccessful response`(): Unit = runBlocking {
    val exception = Exception("Error while searching cities")
    // Mock an unsuccessful response
    Mockito.`when`(mockResponse.isSuccessful).thenThrow(exception)
    Mockito.`when`(mockApiService.searchCities(Mockito.anyString())).thenReturn(mockResponse)

    // Call the function (expecting an exception)
    val result = mockRepository.searchCities(query)

    assertEquals(0, result.size)
    assertEquals(exception, result)
  }

}

private const val query = "London"
private val cityDto1 = CityDto(
  idCity = 1,
  nameCity = "London",
  regionCity = "England",
  countryCity = "United Kingdom",
  lat = 51.5074,
  lon = 0.1278,
)
private val cityDto2 = CityDto(
  idCity = 2,
  nameCity = "Londonderry",
  regionCity = "Northern Ireland",
  countryCity = "United Kingdom",
  lat = 55.0074,
  lon = -7.3078
)