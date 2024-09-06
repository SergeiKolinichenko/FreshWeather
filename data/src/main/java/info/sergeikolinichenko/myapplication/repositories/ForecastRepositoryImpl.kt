package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.repositories.ForecastRepository
import info.sergeikolinichenko.myapplication.mappers.mapToForecast
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.DAYS_OF_WEATHER_KEY
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.SETTINGS_KEY
import javax.inject.Inject

class ForecastRepositoryImpl @Inject constructor(
  private val preferences: SharedPreferences
) : ForecastRepository {

  override suspend fun getForecast(cities: List<City>): Result<List<Forecast>> =
    runCatching {
      val days = preferences.getInt(DAYS_OF_WEATHER_KEY, SEVEN_DAYS_FORECAST).toString()
      val settings = getMySettings()

      cities.map { city ->
        val location = "${city.lat}, ${city.lon}"

        val response = ApiFactory.apiServiceForVisualcrossing.getCurrentWeather(
          location = location,
          days = days
        )

        if (response.isSuccessful) response.body()!!.mapToForecast(city.id, settings)
        else throw Exception(response.code().toString())
      }
    }

  internal fun getMySettings(): Settings {

    val jsonObject = preferences.getString(SETTINGS_KEY, null)

    return jsonObject?.let {
      val settings = Gson().fromJson(jsonObject, Settings::class.java)
      settings
    } ?: Settings(
      temperature = TEMPERATURE.CELSIUS,
      precipitation = PRECIPITATION.MM,
      pressure = PRESSURE.HPA
    )
  }

  companion object {
    private const val SEVEN_DAYS_FORECAST = 7
  }
}