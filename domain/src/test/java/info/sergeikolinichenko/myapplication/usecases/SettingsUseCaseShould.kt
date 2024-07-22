package info.sergeikolinichenko.myapplication.usecases

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.repositories.SettingsRepository
import info.sergeikolinichenko.domain.usecases.settings.SettingsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

/** Created by Sergei Kolinichenko on 17.07.2024 at 14:47 (GMT+3) **/

class SettingsUseCaseShould {

  // region constants
  private val repository = mock<SettingsRepository>()
  private val settings = mock<Settings>()
  // endregion constants

  // system under test
  val SUT = SettingsUseCase(repository)

  @Test
  fun `set settings to shared preferences`() {
    // Arrange
    // Act
    SUT.setSettings(settings)
    // Assert
    verify(repository, times(1)).setSettings(settings)
  }

  @Test
  fun `get settings to shared preferences`() {
    // Arrange
    // Act
    SUT.getSettings()
    // Assert
    verify(repository, times(1)).getSettings()
  }


  @Test
  fun `get the same settings from shared preferences`() = runTest {
    // Arrange
    whenever(repository.getSettings()).thenReturn(flow { emit(settings) })
    // Act
    SUT.setSettings(settings)
    // Assert
    Assert.assertEquals(settings, SUT.getSettings().first())
  }


}