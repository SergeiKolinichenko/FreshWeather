package info.sergeikolinichenko.domain.repositories

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:58 (GMT+3) **/

interface SearchRepository {
  suspend fun <T> searchCities(query: String): List<T>
}