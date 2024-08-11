package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDao
import info.sergeikolinichenko.myapplication.mappers.mapToForecast
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.CurrentWeatherDto
import info.sergeikolinichenko.myapplication.network.dto.DayForecastDto
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.HourForecastDto
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock


/** Created by Sergei Kolinichenko on 17.06.2024 at 21:03 (GMT+3) **/

class WeatherRepositoryImplShould {

  private lateinit var repository: WeatherRepositoryImpl
  private lateinit var mockApiService: ApiService
  private lateinit var mockSharedPreferences: SharedPreferences
  private lateinit var mockApiFactory: ApiFactory

  @Before
  fun setup() {
    mockApiService = mock()
    mockSharedPreferences = mock()
    mockApiFactory = mock {
      on { getVisualcrossingApi() } doReturn mockApiService
    }
    repository = WeatherRepositoryImpl(mockApiFactory, mockSharedPreferences)
  }


  @Test
  fun `getWeather returns success with Weather data`() = runBlocking {
    // Arrange
    val city = testCity
    val settings = Settings(
      temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    )
    val dayForecastDto = mock<DayForecastDto> {
      on { icon } doReturn "test-icon" // Provide a non-null value for icon
      on { tempMax } doReturn 20.0f
      on { tempMin } doReturn 10.0f
    }
    val forecastDto = ForecastDto(
      currentWeatherDto = mock(),
      daysForecast = listOf(dayForecastDto), // Use the mock DayForecastDto
      description ="Partly cloudy",
      timeZone = "Europe/London"
    )
    val expectedWeather = Weather(
      temp = forecastDto.currentWeatherDto.temp.toCelsiusString(),
      maxTemp = forecastDto.daysForecast.first().tempMax.toCelsiusString(),
      minTemp = forecastDto.daysForecast.first().tempMin.toCelsiusString(),
      description = forecastDto.description,
      condIconUrl = forecastDto.daysForecast.first().icon
    )
    val response = Response.success(forecastDto)

    doReturn(response).`when`(mockApiService).getCurrentWeather("${city.lat}, ${city.lon}", "1")
    doReturn(Gson().toJson(settings)).`when`(mockSharedPreferences).getString("settings_key", null)

    // Act
    val result = repository.getWeather(city)

    // Assert
    assertEquals(true, result.isSuccess)
    assertEquals(expectedWeather, result.getOrNull())
  }

  @Test
  fun `getWeather returns failure when response is not successful`() = runBlocking {
    // Arrange
    val city = testCity
    val response = Response.error<ForecastDto>(404, mock())

    doReturn(response).`when`(mockApiService).getCurrentWeather("${city.lat}, ${city.lon}", "1")

    // Act
    val result = repository.getWeather(city)

    // Assert
    assertEquals(true, result.isFailure)
    assertEquals("404", result.exceptionOrNull()?.message)
  }

  @Test
  fun `getForecast returns success with Forecast data`() = runBlocking {
    // Arrange
    val city = testCity
    val settings = Settings(temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    )

    val hourForecastDto = mock<HourForecastDto> {
      on { icon } doReturn "test-icon" // Provide a non-null value for icon
      on { datetimeEpoch } doReturn 1678876800
      on { temp } doReturn 18.0f
      on { precipProb } doReturn 20.0f
      on { humidity } doReturn 70.0f
      on { pressure } doReturn 1012.0f
      on { uvIndex } doReturn 3
    }

    val currentWeatherDto = mock<CurrentWeatherDto> {
      on { temp } doReturn 20.0f
      on { feelsLike } doReturn 18.0f
      on { humidity } doReturn 60.0f
      on { windSpeed } doReturn 15.0f
      on { windDir } doReturn 15.0f
      on { precipProb } doReturn 20.0f
      on { precip } doReturn 1.5f
      on { precipType } doReturn listOf("rain")
      on { pressure } doReturn 1012.0f
      on { uvIndex } doReturn 3
      on { cloudCover } doReturn 40.0f
      on { icon } doReturn "partly-cloudy-day"
    }
    val dayForecastDto = mock<DayForecastDto> {
      on { datetimeEpoch } doReturn 1678876800
      on { temp } doReturn 18.0f
      on { tempMax } doReturn 22.0f
      on { tempMin } doReturn 14.0f
      on { humidity } doReturn 70.0f
      on { windSpeed } doReturn 10.0f
      on { windDir } doReturn 10.0f
      on { pressure } doReturn 1015.0f
      on { uvIndex } doReturn 4
      on { cloudCover } doReturn 50.0f
      on { precipProb } doReturn 30.0f
      on { precip } doReturn 2.5f
      on { precipType } doReturn listOf("rain", "snow")
      on { description } doReturn "Partly cloudy with a chance of rain"
      on { icon } doReturn "partly-cloudy-day"
      // ... other properties
      on { hoursForecast } doReturn (listOf(hourForecastDto))
    }
    val forecastDto = ForecastDto(
      currentWeatherDto = currentWeatherDto,
      daysForecast = listOf(dayForecastDto),
      description = "Partly cloudy",
      timeZone = "Europe/London"
    )
    val expectedForecast = forecastDto.mapToForecast(settings)
    val response = Response.success(forecastDto)

    doReturn(response).`when`(mockApiService).getCurrentWeather("${city.lat}, ${city.lon}", "7")
    doReturn(Gson().toJson(settings)).`when`(mockSharedPreferences).getString("settings_key", null)

    // Act
    val result = repository.getForecast(city)

    // Assert
    assertEquals(true, result.isSuccess)
    assertEquals(expectedForecast, result.getOrNull())
  }

  @Test
  fun `getForecast returns failure when response is not successful`() = runBlocking {
    // Arrange
    val city = testCity
    val response = Response.error<ForecastDto>(500, mock())

    doReturn(response).`when`(mockApiService).getCurrentWeather("${city.lat}, ${city.lon}","7")

    // Act
    val result = repository.getForecast(city)

    // Assert
    assertEquals(true, result.isFailure)
    assertEquals("500", result.exceptionOrNull()?.message)
  }

}