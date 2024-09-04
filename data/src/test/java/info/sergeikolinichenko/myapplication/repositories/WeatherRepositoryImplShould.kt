package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.mappers.mapForecastDtoToWeather
import info.sergeikolinichenko.myapplication.mappers.mapToForecast
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.DAYS_OF_WEATHER_KEY
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.SETTINGS_KEY
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.testForecastDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import org.mockito.kotlin.mock


/** Created by Sergei Kolinichenko on 17.06.2024 at 21:03 (GMT+3) **/

class WeatherRepositoryImplShould {

  @Mock private lateinit var mockRepository: WeatherRepositoryImpl
  @Mock private lateinit var mockApiService: ApiService
  @Mock private lateinit var mockSharedPreferences: SharedPreferences
  @Mock private lateinit var mockResponse: Response<ForecastDto>

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    mockApiService = mock()
    mockSharedPreferences = mock()
    ApiFactory.apiServiceForVisualcrossing = mockApiService
    mockRepository = WeatherRepositoryImpl(mockSharedPreferences)
  }


  @Test
  fun `getWeather returns success with Weather data`(): Unit = runBlocking {
    // Arrange
    val city = testCity
    val settings = Settings(
      temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    )
    val expectedWeather = testForecastDto.mapForecastDtoToWeather(settings)

    Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
    Mockito.`when`(mockResponse.body()).thenReturn(testForecastDto)
    Mockito.`when`(mockApiService.getCurrentWeather(
      Mockito.anyString(),
      Mockito.anyString()
    )).thenReturn(mockResponse)
    Mockito.`when`(mockSharedPreferences.getString("settings_key", null)).thenReturn(Gson().toJson(settings))

    // Act
    val result = mockRepository.getWeather(city)

    // Assert
    assertEquals(true, result.isSuccess)
    assertEquals(expectedWeather, result.getOrNull())
  }

  @Test
  fun `getWeather returns failure on unsuccessful response`() = runBlocking {
    Mockito.`when`(mockResponse.isSuccessful).thenReturn(false)
    Mockito.`when`(mockResponse.code()).thenReturn(400)
    Mockito.`when`(
      mockApiService.getCurrentWeather(Mockito.anyString(), Mockito.anyString())
    ).thenReturn(mockResponse)

    val city = testCity
    val result = mockRepository.getWeather(city)

    assertEquals(true, result.isFailure)
    assertEquals("400", (result.exceptionOrNull() as Exception).message)
  }

  @Test
  fun `getForecast returns success on successful response`() = runBlocking {
    val forecastDto = testForecastDto
    val settings = Settings(
      temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    )
    val expectedForecast = forecastDto.mapToForecast(settings)

    Mockito.`when`(mockResponse.isSuccessful).thenReturn(true)
    Mockito.`when`(mockResponse.body()).thenReturn(forecastDto)
    Mockito.`when`(
      mockApiService.getCurrentWeather(
        Mockito.anyString(),
        Mockito.anyString()
      )
    ).thenReturn(mockResponse)
    Mockito.`when`(mockSharedPreferences.getString("settings_key", null)).thenReturn(Gson().toJson(settings))
    Mockito.`when`(mockSharedPreferences.getInt(DAYS_OF_WEATHER_KEY, 7))
      .thenReturn(7)

    val city = testCity
    val result = mockRepository.getForecast(city)

    assertEquals(true, result.isSuccess)
    assertEquals(expectedForecast, result.getOrNull())
  }

  @Test
  fun `getForecast returns failure on unsuccessful response`() = runBlocking {
    Mockito.`when`(mockResponse.isSuccessful).thenReturn(false)
    Mockito.`when`(mockResponse.code()).thenReturn(400)
    Mockito.`when`(
      mockApiService.getCurrentWeather(
        Mockito.anyString(),
        Mockito.anyString()
      )
    ).thenReturn(mockResponse)

    Mockito.`when`(mockSharedPreferences.getInt(DAYS_OF_WEATHER_KEY, 7))
      .thenReturn(7)

    val city = testCity
    val result = mockRepository.getForecast(city)

    assertEquals(true, result.isFailure)
    assertEquals("400", (result.exceptionOrNull() as Exception).message)
  }

  @Test
  fun `getMySettings returns settings from preferences`() {
    val settings = Settings(
      temperature = TEMPERATURE.FAHRENHEIT,
      precipitation = PRECIPITATION.INCHES,
      pressure = PRESSURE.MMHG
    )
    val jsonString = Gson().toJson(settings)
    Mockito.`when`(mockSharedPreferences.getString(SETTINGS_KEY, null)).thenReturn(jsonString)

    val result = mockRepository.getMySettings()

    assertEquals(settings, result)
  }

  @Test
  fun `getMySettings returns default settings when preferences are empty`() {
    Mockito.`when`(mockSharedPreferences.getString(SETTINGS_KEY, null)).thenReturn(null)

    val result = mockRepository.getMySettings()

    assertEquals(
      Settings(
        temperature = TEMPERATURE.CELSIUS,
        precipitation = PRECIPITATION.MM,
        pressure = PRESSURE.HPA
      ), result
    )
  }

}