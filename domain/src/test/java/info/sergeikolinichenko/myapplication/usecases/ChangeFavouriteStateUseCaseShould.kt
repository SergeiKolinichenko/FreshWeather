package info.sergeikolinichenko.myapplication.usecases

import com.nhaarman.mockitokotlin2.verify
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.usecases.ChangeFavouriteStateUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

/** Created by Sergei Kolinichenko on 15.06.2024 at 16:31 (GMT+3) **/

class ChangeFavouriteStateUseCaseShould {

  // region constants
  private val repository = mock<FavouriteRepository>()
  private val city = mock<City>()
  private val SUT = ChangeFavouriteStateUseCase(repository)
  private val id = 1
  // endregion constants

  @Test
  fun `add favourite city to db`() = runBlocking {
    // Act
    SUT.addToFavourite(city)
    // Assert
    verify(repository, Mockito.times(1)).setToFavourite(city)
  }
  @Test
  fun `remove favourite city from db`() = runBlocking {
    // Act
    SUT.removeFromFavourite(id)
    // Assert
    verify(repository, Mockito.times(1)).removeFromFavourite(id)
  }
}