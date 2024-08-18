package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDao
import info.sergeikolinichenko.myapplication.mappers.toCityDbModel
import info.sergeikolinichenko.myapplication.mappers.toListFavouriteCities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
  private val freshWeatherDao: FreshWeatherDao,
  private val preferences: SharedPreferences
) : FavouriteRepository {

  override val getFavouriteCities: Flow<Result<List<City>>>
    get() = freshWeatherDao.getAllCities().map { list ->
      if (list.isEmpty()) {
        Result.failure(RuntimeException(ERROR_NO_CITIES_LIST))
      } else {

        val orderMap = getOrderCitiesViewed()?.withIndex()?.associate { it.value to it.index }

        Result.success(list.toListFavouriteCities()
          .sortedBy { item ->
            orderMap?.get(item.id)
          }
        )
      }
    }

  override suspend fun setToFavourite(city: City) {
    val cityDbModel = city.toCityDbModel()
    freshWeatherDao.addCity(cityDbModel)
  }

  override suspend fun removeFromFavourite(id: Int) {
    freshWeatherDao.removeCityById(id)
  }


  private fun getOrderCitiesViewed(): List<Int>? =
    preferences.getString(ORDER_CITIES_VIEWED, null)?.let {
      Gson().fromJson(it, Array<Int>::class.java).toList()
    }

  override fun setOrderCitiesViewed(order: List<Int>) =
    preferences.edit().putString(ORDER_CITIES_VIEWED, Gson().toJson(order)).apply()

  companion object {
    const val ORDER_CITIES_VIEWED = "order_cities_viewed"
    const val ERROR_NO_CITIES_LIST = "no_cities_list"
  }
}