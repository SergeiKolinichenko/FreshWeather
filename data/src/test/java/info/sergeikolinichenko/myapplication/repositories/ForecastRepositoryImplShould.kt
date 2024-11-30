package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDao
import info.sergeikolinichenko.myapplication.mappers.mapToForecast
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.network.api.ApiService
import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.DAYS_OF_WEATHER_KEY
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.SETTINGS_KEY
import info.sergeikolinichenko.myapplication.utils.testCity
import info.sergeikolinichenko.myapplication.utils.testForecast
import info.sergeikolinichenko.myapplication.utils.testForecastDbModel
import info.sergeikolinichenko.myapplication.utils.testForecastDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import retrofit2.Response

/** Created by Sergei Kolinichenko on 17.06.2024 at 21:03 (GMT+3) **/

class ForecastRepositoryImplShould {

  @Mock private lateinit var mockRepository: ForecastRepositoryImpl
  @Mock private lateinit var mockApiService: ApiService
  @Mock private lateinit var mockDao: FreshWeatherDao
  @Mock private lateinit var mockSharedPreferences: SharedPreferences
  @Mock private lateinit var mockResponse: Response<ForecastDto>

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    mockApiService = mock()
    mockDao = mock()
    mockSharedPreferences = mock()
    ApiFactory.apiServiceForVisualcrossing = mockApiService
    mockRepository = ForecastRepositoryImpl(mockDao, mockSharedPreferences)
  }

  @Test
  fun `getForecast from net returns success on successful response`() = runTest {
    val forecastDto = testForecastDto
    val settings = Settings(
      temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    )
    val expectedForecast = forecastDto.mapToForecast( testCity.id, settings)

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

    val cities = listOf(testCity)
    val result = mockRepository.getForecastsFromNet(cities)

    assertEquals(true, result.isSuccess)
    assertEquals(expectedForecast, result.getOrNull()?.first())
  }

  @Test
  fun `getForecast from net returns failure on unsuccessful response`() = runBlocking {
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

    val cities = listOf(testCity)
    val result = mockRepository.getForecastsFromNet(cities)

    assertEquals(true, result.isFailure)
    assertEquals("400", (result.exceptionOrNull() as Exception).message)
  }

  @Test
  fun `getForecast from db returns success on successful response`() = runTest {
    // Arrange
    val listForecastDbModel = listOf(testForecastDbModel)
    Mockito.`when`(mockDao.getForecastsFromDb()).thenReturn(flow { emit(listForecastDbModel)})
    // Act
    val result = mockRepository.getForecastsFromDb.first()
    // Assert
    Assert.assertTrue(result.isSuccess)
    assertEquals(testForecast, result.getOrNull()?.first())
  }

  @Test
  fun `getForecast from db returns failure on exception`() = runTest {
    // Arrange
    Mockito.`when`(mockDao.getForecastsFromDb()).thenReturn(
      flow { emit(emptyList()) }
    )
    // Act
    val result = mockRepository.getForecastsFromDb.first()
    // Assert
    assertTrue(result.isFailure)
    assertEquals("No forecast in db", result.exceptionOrNull()?.message)
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