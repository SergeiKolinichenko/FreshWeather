package info.sergeikolinichenko.myapplication.local.db

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import kotlinx.coroutines.flow.Flow

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:27 (GMT+3) **/
@Dao
interface FreshWeatherDao {
  @Query("SELECT * FROM favourite_cities")
  fun getAllCities(): Flow<List<CityDbModel>>

  @Query("SELECT EXISTS (SELECT * FROM favourite_cities WHERE id = :id LIMIT 1)")
  fun observeIsFavourite(id: Int): Flow<Boolean>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addCity(city: CityDbModel)

  @Query("DELETE FROM favourite_cities WHERE id = :id")
  suspend fun removeCityById(id: Int)
}