package info.sergeikolinichenko.myapplication.repositories

import android.content.SharedPreferences
import android.os.Build
import com.google.gson.Gson
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Created by Sergei Kolinichenko on 20.07.2024 at 20:38 (GMT+3) **/

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class SettingsRepositoryImplShould : BaseUnitTestsRules() {

  private val preferences = mock(SharedPreferences::class.java)
  private val editor = mock(SharedPreferences.Editor::class.java)
  private val gson = Gson()

  private val SUT = SettingsRepositoryImpl(preferences)

  @Test
  fun `test set settings`() {
    // Arrange
    val settings = Settings(TEMPERATURE.CELSIUS, PRECIPITATION.MM, PRESSURE.HPA)
    val jsonObject = gson.toJson(settings)
    `when`(preferences.edit()).thenReturn(editor)
    `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
    // Act
    SUT.setSettings(settings)
    // Assert
    verify(editor).putString(SettingsRepositoryImpl.SETTINGS_KEY, jsonObject) // Check key and value
    verify(editor).apply() // Check that apply() was called
  }

  @Test
  fun `test get settings with stored settings`() = runBlocking {
    // Arrange
    val settings = Settings(TEMPERATURE.CELSIUS, PRECIPITATION.MM, PRESSURE.HPA)
    val jsonObject = gson.toJson(settings)
    `when`(preferences.getString(SettingsRepositoryImpl.SETTINGS_KEY, null)).thenReturn(jsonObject)

    // Act
    val flow: Flow<Settings> = SUT.getSettings()
    val result = flow.first()

    // Assert
    assertEquals(settings, result)
  }

  @Test
  fun `test get settings without stored settings`() = runBlocking {
    // Arrange
    `when`(preferences.getString(SettingsRepositoryImpl.SETTINGS_KEY, null)).thenReturn(null)

    // Act
    val flow: Flow<Settings> = SUT.getSettings()
    val result = flow.first()

    // Assert
    assertEquals(Settings(TEMPERATURE.CELSIUS, PRECIPITATION.MM, PRESSURE.HPA), result)
  }

}