package info.sergeikolinichenko.myapplication.usecases

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.repositories.FavouriteRepository
import info.sergeikolinichenko.domain.usecases.ObserveFavouriteStateUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

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
  fun `return favourite state is true`() = runBlocking {
    // Arrange
    whenever(repository.observeIsFavourite(id)).thenReturn(flowOf(true))
    // Act
    val actual = SUT.invoke(id)
    // Assert
    Assert.assertTrue(actual.first())
  }
  @Test
  fun `return favourite state is false`(): Unit = runBlocking {
    // Arrange
    whenever(repository.observeIsFavourite(id)).thenReturn(flowOf(false))
    //
    val actual = SUT.invoke(id)
    // Assert
    Assert.assertFalse(actual.first())
  }
}