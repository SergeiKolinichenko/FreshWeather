package info.sergeikolinichenko.data.repositories

import info.sergeikolinichenko.data.local.db.CitiesDao
import info.sergeikolinichenko.data.mappers.toCityDbModel
import info.sergeikolinichenko.data.mappers.toListCities
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
  private val citiesDao: CitiesDao
): FavouriteRepository {
  override val favouriteCities: Flow<List<City>>
    get() = citiesDao.getAllCities().map { it.toListCities() }

  override fun observeIsFavourite(id: Int): Flow<Boolean> =
    citiesDao.observeIsFavourite(id)


  override suspend fun setToFavourite(city: City) {
    val cityDbModel = city.toCityDbModel()
    citiesDao.addCity(cityDbModel)
  }

  override suspend fun removeFromFavourite(id: Int) {
    citiesDao.removeCityById(id)
  }
}