package info.sergeikolinichenko.myapplication

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotSelected
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import info.sergeikolinichenko.myapplication.utils.BaseUiTest
import info.sergeikolinichenko.myapplication.utils.withRole
import org.junit.Test

/** Created by Sergei Kolinichenko on 19.07.2024 at 19:55 (GMT+3) **/

class SettingsContentShould : BaseUiTest() {

  @Test
  fun openDropDownMenu() {
    // Arrange
    // Act
    composeTestRule.onNodeWithContentDescription(getString(R.string.favourite_content_description_menu_icon))
      .performClick()
    // Assert
    composeTestRule.onNodeWithText(getString(R.string.dropdown_menu_title_screen_settings))
      .assertIsDisplayed()
  }

  @Test
  fun openSettingsScreen() {
    // Arrange
    // Act
    composeTestRule.onNodeWithContentDescription(getString(R.string.favourite_content_description_menu_icon))
      .performClick()
    composeTestRule.onNodeWithText(getString(R.string.dropdown_menu_title_screen_settings))
      .performClick()
    // Assert
    composeTestRule.onNodeWithText(getString(R.string.settings_content_title_settings))
      .assertIsDisplayed()
  }

  @Test
  fun checkBlockOfTemperatureOfSettingsScreen() {
    // Arrange
    val stateButtonCelsius = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Degrees Celsius (°C)")).and(isSelected())
    ).isDisplayed()

    val stateButtonFahrenheit = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Degrees Fahrenheit (°F)")).and(isSelected())
    ).isDisplayed()

    val buttonCelsius = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Degrees Celsius (°C)"))
    )

    val buttonFahrenheit = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Degrees Fahrenheit (°F)"))
    )

    // Act
    composeTestRule.onNodeWithContentDescription(getString(R.string.favourite_content_description_menu_icon))
      .performClick()
    composeTestRule.onNodeWithText(getString(R.string.dropdown_menu_title_screen_settings))
      .performClick()
    // Assert
    when {
      stateButtonCelsius -> {
        buttonCelsius.assert(isSelected())
        buttonFahrenheit.assert(isNotSelected())

        buttonFahrenheit.performClick()

        buttonCelsius.assert(isNotSelected())
        buttonFahrenheit.assert(isSelected())
      }

      stateButtonFahrenheit -> {
        buttonCelsius.assert(isNotSelected())
        buttonFahrenheit.assert(isSelected())

        buttonCelsius.performClick()

        buttonCelsius.assert(isSelected())
        buttonFahrenheit.assert(isNotSelected())
      }
    }
  }

  @Test
  fun checkBlockOfPrecipitationOfSettingsScreen() {
    // Arrange
    val stateButtonInches = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Inches (inch)")).and(isSelected())
    ).isDisplayed()

    val stateButtonMillimeters = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Millimeters (mm)")).and(isSelected())
    ).isDisplayed()

    val buttonInches = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Inches (inch)"))
    )

    val buttonMillimeters = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Millimeters (mm)"))
    )

    // Act
    composeTestRule.onNodeWithContentDescription(getString(R.string.favourite_content_description_menu_icon))
      .performClick()
    composeTestRule.onNodeWithText(getString(R.string.dropdown_menu_title_screen_settings))

    // Assert
    when {
      stateButtonInches -> {
        buttonInches.assert(isSelected())
        buttonMillimeters.assert(isNotSelected())

        buttonMillimeters.performClick()

        buttonInches.assert(isNotSelected())
        buttonMillimeters.assert(isSelected())
      }

      stateButtonMillimeters -> {
        buttonInches.assert(isNotSelected())
        buttonMillimeters.assert(isSelected())

        buttonInches.performClick()

        buttonInches.assert(isSelected())
        buttonMillimeters.assert(isNotSelected())
      }
    }
  }

  @Test
  fun checkBlockOfPressureOfSettingsScreen() {
    // Arrange
    val stateButtonMmHg = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Millimetres of mercury column (mmHg)"))
        .and(isSelected())
    ).isDisplayed()
    val stateButtonHpa = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Hectopascals (hPa)")).and(isSelected())
    ).isDisplayed()
    val buttonMmHg = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Millimetres of mercury column (mmHg)"))
    )
    val buttonHpa = composeTestRule.onNode(
      withRole(Role.RadioButton).and(hasTestTag("Hectopascals (hPa)"))
    )

    // Act
    composeTestRule.onNodeWithContentDescription(getString(R.string.favourite_content_description_menu_icon))
      .performClick()
    composeTestRule.onNodeWithText(getString(R.string.dropdown_menu_title_screen_settings))

    // Assert
    when {
      stateButtonMmHg -> {
        buttonMmHg.assert(isSelected())
        buttonHpa.assert(isNotSelected())
        buttonMmHg.performClick()
        buttonMmHg.assert(isNotSelected())
        buttonHpa.assert(isSelected())
      }

      stateButtonHpa -> {
        buttonMmHg.assert(isNotSelected())
        buttonHpa.assert(isSelected())
        buttonMmHg.performClick()
        buttonMmHg.assert(isSelected())
        buttonHpa.assert(isNotSelected())
      }
    }
  }

  @Test
  fun checkButtonEvaluateTheApplication() {
    // Arrange
    composeTestRule.onNodeWithContentDescription(getString(R.string.favourite_content_description_menu_icon))
      .performClick()
    composeTestRule.onNodeWithText(getString(R.string.dropdown_menu_title_screen_settings))
      .performClick()
    // Act
    composeTestRule.onNodeWithText(getString(R.string.settings_content_unit_evaluate_the_application)).assertIsDisplayed()
    composeTestRule.onNodeWithText(getString(R.string.settings_content_unit_evaluate_the_application))
      .performClick()
    // Assert
    // TODO I'll write later when I figure out how
  }

  @Test
  fun checkButtonWriteToDevelopers() {
    // Arrange
    composeTestRule.onNodeWithContentDescription(getString(R.string.favourite_content_description_menu_icon))
      .performClick()
    composeTestRule.onNodeWithText(getString(R.string.dropdown_menu_title_screen_settings))
      .performClick()
    // Act
    composeTestRule.onNodeWithText(getString(R.string.settings_content_unit_write_to_the_developers))
      .assertIsDisplayed()
    composeTestRule.onNodeWithText(getString(R.string.settings_content_unit_write_to_the_developers))
      .performClick()
    // Assert
    // TODO I'll write later when I figure out how
  }

  // region methods
  private fun getString(id: Int) = composeTestRule.activity.getString(id)
  // endregion methods
}

//    composeTestRule.onRoot(useUnmergedTree = false).printToLog("MyLog")