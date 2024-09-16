package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.myapplication.local.db.FreshWeatherDao
import info.sergeikolinichenko.myapplication.mappers.mapCityToDbModel
import info.sergeikolinichenko.myapplication.mappers.mapListDbModelsToListCities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
  private val dao: FreshWeatherDao,
  private val preferences: SharedPreferences
) : FavouriteRepository {

  override val getFavouriteCities: Flow<Result<List<City>>> get() =

    dao.getAllCities().map { list ->
      if (list.isEmpty()) {
        Result.failure(RuntimeException(ERROR_NO_CITIES_LIST))
      } else {

        val orderMap = getOrderCitiesViewed()?.withIndex()?.associate { it.value to it.index }

        Result.success(list.mapListDbModelsToListCities()
          .sortedBy { item ->
            orderMap?.get(item.id)
          }
        )
      }
    }

  override suspend fun setToFavourite(city: City) {
    val cityDbModel = city.mapCityToDbModel()
    dao.addCity(cityDbModel)
  }

  override suspend fun removeFromFavourite(id: Int) {
    dao.removeCityById(id)
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