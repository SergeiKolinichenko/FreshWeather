package info.sergeikolinichenko.domain.repositories

import info.sergeikolinichenko.domain.entity.City
import kotlinx.coroutines.flow.Flow


/** Created by Sergei Kolinichenko on 21.02.2024 at 16:37 (GMT+3) **/

interface FavouriteRepository {

  val getFavouriteCities: Flow<List<City>>
  fun observeIsFavourite(id: Int): Flow<Boolean>
  suspend fun setToFavourite(city: City)
  suspend fun removeFromFavourite(id: Int)
}