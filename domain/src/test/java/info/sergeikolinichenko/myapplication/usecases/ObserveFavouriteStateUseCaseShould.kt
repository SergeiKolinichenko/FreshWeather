package info.sergeikolinichenko.myapplication.usecases

import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.usecases.favourite.ObserveFavouriteStateUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Created by Sergei Kolinichenko on 16.06.2024 at 13:57 (GMT+3) **/

class ObserveFavouriteStateUseCaseShould {
  // region constants
  private val repository = mock<FavouriteRepository>()
  private val id = 1
  private val SUT = ObserveFavouriteStateUseCase(repository)

  // endregion  constants
  @Test
  fun `get favourite state`() {
    // Act
    SUT.invoke(id)
    // Assert
    verify(repository, times(1)).observeIsFavourite(id)
  }

  @Test
  fun `return favourite state is true`() = runTest {
    // Arrange
    whenever(repository.observeIsFavourite(id)).thenReturn(flowOf(true))
    // Act
    val actual = SUT.invoke(id)
    // Assert
    Assert.assertTrue(actual.first())
  }

  @Test
  fun `return favourite state is false`(): Unit = runTest {
    // Arrange
    whenever(repository.observeIsFavourite(id)).thenReturn(flowOf(false))
    //
    val actual = SUT.invoke(id)
    // Assert
    Assert.assertFalse(actual.first())
  }
}