package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.usecases.favourite.ChangeFavouriteStateUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

/** Created by Sergei Kolinichenko on 15.06.2024 at 16:31 (GMT+3) **/

class ChangeFavouriteStateUseCaseShould {

  // region constants
  private val repository = mock<FavouriteRepository>()
  private val city = mock<City>()
  private val id = 1
  // endregion constants

  private val systemUnderTest = ChangeFavouriteStateUseCase(repository)
  @Test
  fun `add favourite city to db`() = runBlocking {
    // Act
    systemUnderTest.addToFavourite(city)
    // Assert
    verify(repository, Mockito.times(1)).setToFavourite(city)
  }
  @Test
  fun `remove favourite city from db`() = runTest {
    // Act
    systemUnderTest.removeFromFavourite(id)
    // Assert
    verify(repository, Mockito.times(1)).removeFromFavourite(id)
  }
}