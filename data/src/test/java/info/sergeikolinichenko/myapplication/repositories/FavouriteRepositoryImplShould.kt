package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDao
import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.testCityDbModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 19.06.2024 at 15:41 (GMT+3) **/

class FavouriteRepositoryImplShould : BaseUnitTestsRules() {
  // region constants
  private val dao = mock<FreshWeatherDao>()
  private val preferences = mock<SharedPreferences>()
  private val exceptionMessage = "no_cities_list"
  private val id = 1
  // endregion constants

  private val SUT = FavouriteRepositoryImpl(dao, preferences)

  @Test
  fun `get favourite cities from Db`(): Unit = runBlocking {
    // Act
    SUT.getFavouriteCities
    // Assert
    verify(dao, times(1)).getAllCities()
  }

  @Test
  fun `get list of favourite cities from Db with map`(): Unit = runTest {
    // Arrange
    whenever(dao.getAllCities()).thenReturn( flow { emit(listOf(testCityDbModel)) })
    val sample = testCityDbModel.toCityInTest()
    // Act
    val result = SUT.getFavouriteCities.first().getOrNull()?.first()
    // Assert
    assert(result == sample)
  }

  @Test
  fun `get exception from Db when error`(): Unit = runTest {
    // Arrange
    whenever(dao.getAllCities()).thenReturn ( flow { emit(emptyList() ) } )
    // Act
    val result = SUT.getFavouriteCities.first().exceptionOrNull()?.message
    // Assert
    assert(result == exceptionMessage)
  }

  @Test
  fun `save city to favorite`(): Unit = runBlocking {
    // Arrange
    val city = testCityDbModel.toCityInTest()
    // Act
    SUT.setToFavourite(city)
    // Assert
    verify(dao, times(1)).addCity(testCityDbModel)
  }
  @Test
  fun `remove city from favorite`(): Unit = runBlocking {
    // Act
    SUT.removeFromFavourite(id)
    // Assert
    verify(dao, times(1)).removeCityById(id)
  }

  // region helper functions
  private fun CityDbModel.toCityInTest(): City {
    return City(
      id = id,
      name = name,
      country = country,
      region = region,
      lat = lat,
      lon = lon
    )
  }
  // endregion helper functions

}