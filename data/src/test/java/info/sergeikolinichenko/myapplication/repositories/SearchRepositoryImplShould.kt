package info.sergeikolinichenko.myapplication.repositories

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.CityDto
import info.sergeikolinichenko.myapplication.utils.cityDto
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Test
import retrofit2.Response

/** Created by Sergei Kolinichenko on 19.06.2024 at 14:38 (GMT+3) **/

class SearchRepositoryImplShould {
  // region constants
  private val apiService = mock<ApiService>()
  private val query = "query"
  private val exception = Exception("Some kind of message")
  // endregion constants

  private val SUT = SearchRepositoryImpl(apiService)

  @Test
  fun `get Response with List of CityDto from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.searchCities(query)).thenReturn(Response.success(listOf(cityDto)))
    // Act
    SUT.searchCities(query)
    // Assert
    verify(apiService, times(1)).searchCities(query)
  }
  @Test
  fun `return List of CityDto from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.searchCities(query)).thenReturn(Response.success(listOf(cityDto)))
    // Act
    val result = SUT.searchCities(query)
    // Assert
    assert(result.first() == listOf(cityDto).first().toCity())
  }
  @Test
  fun `get error of receiving List of CityDto from ApiService`(): Unit = runBlocking {
    // Arrange
    whenever(apiService.searchCities(query)).thenReturn(Response.error(
      400,
      "".toByteArray().toResponseBody(null)
    ))
    // Act
    val thrown = Assert.assertThrows(Exception::class.java) { runBlocking { SUT.searchCities(query) } }
    // Assert
    assert(thrown.cause == exception.cause)
    assert(thrown.message == "Error while searching cities")
  }

  // region helper functions
  private fun CityDto.toCity(): City {
    return City(
      id = idCity,
      name = nameCity,
      country = countryCity,
      region = regionCity
    )
  }
  // endregion helper functions
}