package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.repositories.ForecastRepository
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDao
import info.sergeikolinichenko.myapplication.mappers.mapListDbModelsToListForecast
import info.sergeikolinichenko.myapplication.mappers.mapListForecastToListDbModel
import info.sergeikolinichenko.myapplication.mappers.mapToForecast
import info.sergeikolinichenko.myapplication.network.api.ApiFactory
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.DAYS_OF_WEATHER_KEY
import info.sergeikolinichenko.myapplication.repositories.SettingsRepositoryImpl.Companion.SETTINGS_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ForecastRepositoryImpl @Inject constructor(
  private val dao: FreshWeatherDao,
  private val preferences: SharedPreferences
) : ForecastRepository {

  override suspend fun getForecastsFromNet(cities: List<City>): Result<List<Forecast>> = runCatching {

      cities.map { city ->

        val response = ApiFactory.apiServiceForVisualcrossing.getCurrentWeather(
          location = "${city.lat}, ${city.lon}",
          days = preferences.getInt(DAYS_OF_WEATHER_KEY, SEVEN_DAYS_FORECAST).toString()
        )

        Log.d("TAG", "getForecastsFromNet: ${response.body()!!.daysForecast.first().moonset}")
        Log.d("TAG", "getForecastsFromNet: ${response.body()!!.daysForecast.first().moonrise}")

        if (response.isSuccessful) response.body()!!.mapToForecast(city.id, getMySettings())
        else throw Exception(response.code().toString())
      }
    }

  override suspend fun insertForecastsToDb(forecasts: List<Forecast>) =
    dao.setForecastIntoDb(forecasts.mapListForecastToListDbModel())

  override val getForecastsFromDb: Flow<Result<List<Forecast>>> get() =
    dao.getForecastsFromDb().map {

      Log.d("TAG", "getForecastsFromDb ${it.first().upcomingDays.first().moonset}")
      Log.d("TAG", "getForecastsFromDb ${it.first().upcomingDays.first().moonrise}")

      if (it.isEmpty()) Result.failure(RuntimeException("No forecast in db"))
      else Result.success(it.mapListDbModelsToListForecast())
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