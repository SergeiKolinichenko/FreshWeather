package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.myapplication.utils.cityDto
import org.junit.Assert.assertEquals
import org.junit.Test

/** Created by Sergei Kolinichenko on 16.06.2024 at 18:42 (GMT+3) **/

class SearchMapperShould {
  @Test
  fun `map CityDto to City`() {
    // Act
    val city = cityDto.toCity()
    // Assert
    assertEquals(cityDto.idCity, city.id)
    assertEquals(cityDto.nameCity, city.name)
    assertEquals(cityDto.regionCity, city.region)
    assertEquals(cityDto.countryCity, city.country)
  }
  @Test
  fun `map list CityDto to list City`() {
    // Arrange
    val listCityDto = listOf(cityDto)
    // Act
    val listCities = listCityDto.toListCities()
    // Assert
    assertEquals(listCityDto.first().idCity, listCities.first().id)
    assertEquals(listCityDto.first().nameCity, listCities.first().name)
    assertEquals(listCityDto.first().regionCity, listCities.first().region)
    assertEquals(listCityDto.first().countryCity, listCities.first().country)
  }
}