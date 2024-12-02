package info.sergeikolinichenko.myapplication.mappers

import info.sergeikolinichenko.myapplication.utils.testFoundDto
import info.sergeikolinichenko.myapplication.utils.testPlaceDto
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.math.abs

/** Created by Sergei Kolinichenko on 30.11.2024 at 16:52 (GMT+3) **/

class CityMapperShould {

  @Test
  fun `test map FoundDto to list of City`() {
    val result = testFoundDto.found.mapListFoundDtoToListCity().first()

    assertEquals(abs(testPlaceDto.placeId.hashCode()), result.id )
    assertEquals(testPlaceDto.city, result.name)
    assertEquals(testPlaceDto.state, result.region)
    assertEquals(testPlaceDto.country, result.country)
    assertEquals(testPlaceDto.lat, result.lat)
    assertEquals(testPlaceDto.lon, result.lon)
  }
}