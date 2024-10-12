package info.sergeikolinichenko.myapplication.store

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.Settings
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.domain.usecases.settings.DaysOfWeatherUseCase
import info.sergeikolinichenko.domain.usecases.settings.SettingsUseCase
import info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.SourceOfOpening
import info.sergeikolinichenko.myapplication.presentation.stors.settings.SettingsStore
import info.sergeikolinichenko.myapplication.presentation.stors.settings.SettingsStoreFactory
import info.sergeikolinichenko.myapplication.utils.BaseUnitTestsRules
import info.sergeikolinichenko.myapplication.utils.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** Created by Sergei Kolinichenko on 21.07.2024 at 17:31 (GMT+3) **/
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
//@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class SettingsStoreFactoryShould: BaseUnitTestsRules() {

  private val storeFactory = DefaultStoreFactory()
  private val mockSettingsUseCase = mock<SettingsUseCase>()
  private val daysOfWeatherUseCase = mock<DaysOfWeatherUseCase>()
  private val sourceOfOpening = mock<SourceOfOpening>()
  private val testSettings = Settings(
    temperature = TEMPERATURE.FAHRENHEIT,
    precipitation = PRECIPITATION.INCHES,
    pressure = PRESSURE.MMHG
  )

  private val systemUnderTest = SettingsStoreFactory(
    storeFactory,
    mockSettingsUseCase,
    daysOfWeatherUseCase
  )

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `test initial state`() = runTest {
    mockSuccessfulCase()
    // Act
    systemUnderTest.create(sourceOfOpening)
    // Assert
    verify(mockSettingsUseCase, times(1)).getSettings()
  }

  @Test
  fun `test settings loaded`() = runTest {
    // Arrange
    mockSuccessfulCase()
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    // Assert
    assertEquals(testSettings.temperature, store.state.temperature)
    assertEquals(testSettings.precipitation, store.state.precipitation)
    assertEquals(testSettings.pressure, store.state.pressure)
  }

  private fun mockSuccessfulCase() {
    `when`(mockSettingsUseCase.getSettings()).thenReturn(flowOf(testSettings))
  }

  @Test
  fun `test change of temperature measure`() = runTest {
    // Arrange
    mockSuccessfulCase()
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    store.accept(SettingsStore.Intent.ChangeOfTemperatureMeasure(TEMPERATURE.FAHRENHEIT))
    // Assert
    assertEquals(TEMPERATURE.FAHRENHEIT, store.state.temperature)
  }

  @Test
  fun `test change of precipitation measure`() = runTest {
    // Arrange
    mockSuccessfulCase()
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    store.accept(SettingsStore.Intent.ChangeOfPrecipitationMeasure(PRECIPITATION.INCHES))
    // Assert
    assertEquals(PRECIPITATION.INCHES, store.state.precipitation)
  }

  @Test
  fun `test change of pressure measure`() = runTest {
    // Arrange
    mockSuccessfulCase()
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    store.accept(SettingsStore.Intent.ChangeOfPressureMeasure(PRESSURE.MMHG))
    // Assert
    assertEquals(PRESSURE.MMHG, store.state.pressure)
  }

  @Test
  fun `test on clicked done`() = runTest {
    // Arrange
    mockSuccessfulCase()
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    val testSettings = Settings(
      temperature = store.state.temperature,
      precipitation = store.state.precipitation,
      pressure = store.state.pressure
    )
    store.accept(SettingsStore.Intent.OnClickedDone(testSettings, 7))
    // Assert
    verify(mockSettingsUseCase, times(1)).setSettings(testSettings)
  }

  @Test
  fun `test on clicked back`() = runTest {
    // Arrange
    mockSuccessfulCase()
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    val testField = store.labels.test()
    store.accept(SettingsStore.Intent.OnClickedBack)
    // Assert
    assertEquals(testField, listOf(SettingsStore.Label.OnBackClicked))
  }

  @Test
  fun `test on clicked evaluate app`() = runTest {
    // Arrange
    mockSuccessfulCase()
    val context = spy(Robolectric.buildActivity(Activity::class.java).get())
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    store.accept(SettingsStore.Intent.OnClickedEvaluateApp(context))
    val startedIntent = shadowOf(context).nextStartedActivity
    val expectedUri = Uri.parse("market://details?id=${context.packageName}")
    // Assert
    Assert.assertEquals(expectedUri, startedIntent.data) // Check the URI
    Assert.assertEquals(Intent.ACTION_VIEW, startedIntent.action) // Check the action
    Assert.assertTrue(startedIntent.flags and Intent.FLAG_ACTIVITY_NO_HISTORY != 0) // Check the NO_HISTORY flag
    Assert.assertTrue(startedIntent.flags and Intent.FLAG_ACTIVITY_NEW_DOCUMENT != 0) // Check the NEW_DOCUMENT flag
    Assert.assertTrue(startedIntent.flags and Intent.FLAG_ACTIVITY_MULTIPLE_TASK != 0) // Check the MULTIPLE_TASK flag
  }

  @Test
  fun `test on clicked evaluate app when Play Store unavailable`() = runTest {
    // Arrange
    mockSuccessfulCase()
    val context = spy(Robolectric.buildActivity(Activity::class.java).get())
    val inOrder = inOrder(context)
    doThrow(ActivityNotFoundException::class.java)
      .`when`(context)
      .startActivity(argThat { action == Intent.ACTION_VIEW && data?.scheme == "market" })
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    store.accept(SettingsStore.Intent.OnClickedEvaluateApp(context))

    inOrder.verify(context).startActivity(argThat{
      action == Intent.ACTION_VIEW && data?.scheme == "market"
    })
    val expectedBrowserUri = Uri.parse("http://play.google.com/store/apps/details?id=${context.packageName}")
    inOrder.verify(context).startActivity(argThat {
      action == Intent.ACTION_VIEW && data == expectedBrowserUri
    })
  }

  @Test
  fun `test clicked write developers`() = runTest {
    // Arrange
    mockSuccessfulCase()
    val context = mock(Context::class.java)
    // Act
    val store = systemUnderTest.create(sourceOfOpening)
    store.accept(SettingsStore.Intent.ClickedWriteDevelopers(context))
    // Assert
    verify(context).startActivity(argThat { action == Intent.ACTION_CHOOSER })

    val capturedIntent = argumentCaptor<Intent>()
    verify(context).startActivity(capturedIntent.capture())

    val innerIntent = capturedIntent.firstValue.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
    assertEquals(Uri.parse("mailto: sergeikolinicenko@gmail.com"), innerIntent?.data)
  }

}