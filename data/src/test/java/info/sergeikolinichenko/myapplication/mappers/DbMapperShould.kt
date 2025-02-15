package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import info.sergeikolinichenko.myapplication.utils.testCityDbModel
import info.sergeikolinichenko.myapplication.utils.testForecast
import org.junit.Assert.assertEquals
import org.junit.Test

/** Created by Sergei Kolinichenko on 16.06.2024 at 18:17 (GMT+3) **/

class DbMapperShould {
  @Test
  fun `map CityDbModel to City`() {
    // Arrange
    // Act
    val city = testCityDbModel.mapDbModelToCity()
    // Assert
    assertEquals(testCityDbModel.id, city.id)
    assertEquals(testCityDbModel.name, city.name)
    assertEquals(testCityDbModel.region, city.region)
    assertEquals(testCityDbModel.country, city.country)
  }
  @Test
  fun `map City to CityDbModel`() {
    // Arrange
    val city = testCityDbModel.toCityTest()
    // Act
    val cityDbModel = city.mapCityToDbModel()
    // Assert
    assertEquals(city.id, cityDbModel.id)
    assertEquals(city.name, cityDbModel.name)
    assertEquals(city.region, cityDbModel.region)
    assertEquals(city.country, cityDbModel.country)
  }
  @Test
  fun `map List of CityDbModel to List of Favourite Cities`() {
    // Arrange
    val cityDbModelList = listOf(testCityDbModel)
    // Act
    val favouriteCities = cityDbModelList.mapListDbModelsToListCities()
    // Assert
    assertEquals(cityDbModelList.first().id, favouriteCities.first().id)
    assertEquals(cityDbModelList.first().name, favouriteCities.first().name)
    assertEquals(cityDbModelList.first().region, favouriteCities.first().region)
    assertEquals(cityDbModelList.first().country, favouriteCities.first().country)
  }

  @Test
  fun `map list Forecast to list ForecastDbModel`() {
    // Arrange
    val listForecast = listOf(testForecast)
    val itemForecast = listForecast.first()
    // Act
    val result = listForecast.mapListForecastToListDbModel().first()
    // Assert
    assert(result.id == itemForecast.id)
    assert(result.tzId == itemForecast.tzId)
    assert(result.currentForecast == itemForecast.currentForecast)
    assert(result.upcomingDays == itemForecast.upcomingDays)
    assert(result.upcomingHours == itemForecast.upcomingHours)
  }

  // region helper functions
  private fun CityDbModel.toCityTest() = City(
      id = id,
      name = name,
      country = country,
      region = region,
      lat = 0.0,
      lon = 0.0
    )
  // endregion helper functions
}