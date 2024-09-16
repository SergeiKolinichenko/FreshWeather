package info.sergeikolinichenko.myapplication.repositories

import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.network.dto.PlaceAddressDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
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
    ApiFactory.apiServiceOpenStreetMap = mockApiService // Inject the mocked API
    mockRepository = SearchRepositoryImpl()
  }

  @Test
  fun `searchCities returns cities on successful response`() = runBlocking {

    // Mock a successful response
    Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
    Mockito.`when`(mockResponse.body()).thenReturn(listOf(cityDto1, cityDto2)) // Mock response body
    Mockito.`when`(mockApiService.searchCities(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(mockResponse)

    // Call the function and assert the result
    val cities: List<CityDto> = mockRepository.searchCities(query)

    assertTrue(cities.isNotEmpty()) // Or more specific assertions based on your toListCities() logic
    assertEquals(2, cities.size)
    assert(cityDto1 == cities[0])
    assert(cityDto2 == cities[1])
  }

  @Test(expected = Exception::class)
  fun `searchCities throws exception on unsuccessful response`(): Unit = runBlocking {
    val exception = Exception("Error while searching cities")
    // Mock an unsuccessful response
    Mockito.`when`(mockResponse.isSuccessful).thenThrow(exception)
    Mockito.`when`(mockApiService.searchCities(Mockito.anyString())).thenReturn(mockResponse)

    // Call the function (expecting an exception)
    val result: List<CityDto> = mockRepository.searchCities(query)

    assertEquals(0, result.size)
    assertEquals(exception, result)
  }

}

private const val query = "London"


private val placeAddressDto1 = PlaceAddressDto(
  state = "England",
  country = "United Kingdom",
  city = "London",
  village = "village",
  town = "town",
)

private val cityDto1 = CityDto(
  id = 1,
  displayName = "London, England, United Kingdom",
  lat = "51.5074",
  lon = "0.1278",
  classType = "classType 1",
  type = "type 1",
  placeAddress = placeAddressDto1
)

private val placeAddressDto2 = PlaceAddressDto(
  state = "Northern Ireland",
  country = "United Kingdom",
  city = "Londonderry",
  village = "village 2",
  town = "town 2",
)

private val cityDto2 = CityDto(
  id = 2,
  displayName = "Londonderry, Northern Ireland, United Kingdom",
  lat = "51.5074",
  lon = "0.1278",
  classType = "classType 2",
  type = "type 2",
  placeAddress = placeAddressDto2
)