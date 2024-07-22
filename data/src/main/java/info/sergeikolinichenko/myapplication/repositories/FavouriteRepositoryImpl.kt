package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.myapplication.local.db.CitiesDao
import info.sergeikolinichenko.myapplication.mappers.toCityDbModel
import info.sergeikolinichenko.myapplication.mappers.toListFavouriteCities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
  private val citiesDao: CitiesDao,
  private val preferences: SharedPreferences
) : FavouriteRepository {

  override val getFavouriteCities: Flow<Result<List<City>>>
    get() = citiesDao.getAllCities().map { list ->
      if (list.isEmpty()) {
        Result.failure(RuntimeException(ERROR_NO_CITIES_LIST))
      } else {
        Result.success(list.toListFavouriteCities().sortedBy { item ->
          getOrderCitiesViewed()?.get(item.id)
        }
        )
      }
    }

  override fun observeIsFavourite(id: Int): Flow<Boolean> =
    citiesDao.observeIsFavourite(id)

  override suspend fun setToFavourite(city: City) {
    val cityDbModel = city.toCityDbModel()
    citiesDao.addCity(cityDbModel)
  }

  override suspend fun removeFromFavourite(id: Int) {
    citiesDao.removeCityById(id)
  }

  override fun setOrderCitiesViewed(order: List<Int>) {
    TODO("Not yet implemented")
  }

  private fun getOrderCitiesViewed(): List<Int>? =
    preferences.getString(ORDER_CITIES_VIEWED, null)?.let {
      Gson().fromJson(it, Array<Int>::class.java).toList()
    }

  companion object {
    private const val ORDER_CITIES_VIEWED = "order_cities_viewed"
    const val ERROR_NO_CITIES_LIST = "no_cities_list"
  }
}