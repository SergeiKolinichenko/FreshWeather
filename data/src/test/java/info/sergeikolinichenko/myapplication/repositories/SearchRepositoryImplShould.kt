package info.sergeikolinichenko.myapplication.repositories

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.FoundDto
import info.sergeikolinichenko.myapplication.utils.testFoundDto
import info.sergeikolinichenko.myapplication.utils.testPlaceDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import retrofit2.Response
import kotlin.math.abs


/** Created by Sergei Kolinichenko on 19.06.2024 at 14:38 (GMT+3) **/

class SearchRepositoryImplShould {

  @Mock private lateinit var mockApiService: ApiService
  @Mock private lateinit var mockResponse: Response<FoundDto> // Use a generic type for the response body
  @Mock private lateinit var mockRepository: SearchRepositoryImpl

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    mockApiService = mock()
    ApiFactory.apiServiceSearch = mockApiService // Inject the mocked API
    mockRepository = SearchRepositoryImpl()
  }

  @Test
  fun `searchCities returns cities on successful response`() = runBlocking {

    // Mock a successful response
    Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
    Mockito.`when`(mockResponse.body()).thenReturn(testFoundDto) // Mock response body
    Mockito.`when`(mockApiService.search(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(mockResponse)

    // Call the function and assert the result
    val result = mockRepository.searchCities(query).first()

    assertEquals(abs(testPlaceDto.placeId.hashCode()), result.id )
    assertEquals(testPlaceDto.city, result.name)
    assertEquals(testPlaceDto.state, result.region)
    assertEquals(testPlaceDto.country, result.country)
    assertEquals(testPlaceDto.lat, result.lat)
    assertEquals(testPlaceDto.lon, result.lon)
  }

  @Test(expected = Exception::class)
  fun `searchCities throws exception on unsuccessful response`(): Unit = runBlocking {
    val exception = Exception("Error while searching cities")
    // Mock an unsuccessful response
    Mockito.`when`(mockResponse.isSuccessful).thenThrow(exception)
    Mockito.`when`(mockApiService.search(Mockito.anyString())).thenReturn(mockResponse)

    // Call the function (expecting an exception)
    val result: List<City> = mockRepository.searchCities(query)

    assertEquals(0, result.size)
    assertEquals(exception, result)
  }

}

private const val query = "London"