package info.sergeikolinichenko.myapplication.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import info.sergeikolinichenko.myapplication.local.models.ForecastDbModel
import kotlinx.coroutines.flow.Flow

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:27 (GMT+3) **/
@Dao
interface FreshWeatherDao {
  @Query("SELECT * FROM favourite_cities")
  fun getAllCities(): Flow<List<CityDbModel>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addCity(city: CityDbModel)

  @Query("DELETE FROM favourite_cities WHERE id = :id")
  suspend fun removeCityById(id: Int)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setForecastIntoDb(forecasts: List<ForecastDbModel>)

  @Query("SELECT * FROM forecast")
  fun getForecastsFromDb(): Flow<List<ForecastDbModel>>
}