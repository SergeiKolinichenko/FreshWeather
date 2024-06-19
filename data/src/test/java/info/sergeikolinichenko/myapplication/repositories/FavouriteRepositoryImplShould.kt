package info.sergeikolinichenko.myapplication.repositories

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.local.db.CitiesDao
import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.cityDbModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

/** Created by Sergei Kolinichenko on 19.06.2024 at 15:41 (GMT+3) **/

class FavouriteRepositoryImplShould : BaseUnitTestsRules() {
  // region constants
  private val dao = mock<CitiesDao>()
  private val id = 1
  // endregion constants

  private val SUT = FavouriteRepositoryImpl(dao)

  @Test
  fun `get favourite cities from Db`(): Unit = runBlocking {
    // Act
    SUT.getFavouriteCities
    // Assert
    verify(dao, times(1)).getAllCities()
  }
  @Test
  fun `load list of favourite cities from Db with map`(): Unit = runBlocking {
    // Arrange
    whenever(dao.getAllCities()).thenReturn(
      flow {
        emit(listOf(cityDbModel))
      }
    )
    val sample = cityDbModel.toCityInTest()
    // Act
    val result = SUT.getFavouriteCities.first().first()
    // Assert
    assert(result == sample)
  }
  @Test
  fun `load true state of observe if city is exist in favorite`(): Unit = runBlocking {
    // Arrange
    whenever(dao.observeIsFavourite(id)).thenReturn(
      flow {
        emit(true)
      }
    )
    // Act
    val result = SUT.observeIsFavourite(id).first()
    // Assert
    assert(result)
  }
  @Test
  fun `load false state of observe if city is not exist in favorite`(): Unit = runBlocking {
    // Arrange
    whenever(dao.observeIsFavourite(id)).thenReturn(
      flow {
        emit(false)
      }
    )
    // Act
    val result = SUT.observeIsFavourite(id).first()
    // Assert
    assert(!result)
  }
  @Test
  fun `save city to favorite`(): Unit = runBlocking {
    // Arrange
    val city = cityDbModel.toCityInTest()
    // Act
    SUT.setToFavourite(city)
    // Assert
    verify(dao, times(1)).addCity(cityDbModel)
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
      region = region
    )
  }
  // endregion helper functions

}