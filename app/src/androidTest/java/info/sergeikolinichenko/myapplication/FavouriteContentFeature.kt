package info.sergeikolinichenko.myapplication

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import info.sergeikolinichenko.myapplication.utils.BaseUiTest
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FavouriteContentFeature : BaseUiTest() {

  @Test
  fun checksIfTheSearchStringExistOnFavouriteScreen() {
    // Arrange
    val string = composeTestRule.activity.getString(R.string.title_few_contents_text_favourite)
    // Assert
    composeTestRule.onNodeWithText(string).assertExists()
  }

  @Test
  fun checksIfTheMenuIconExistOnFavouriteScreen() {
    // Arrange
    val contentDescriptionMenuIcon = composeTestRule.activity.getString(R.string.favourite_content_description_menu_icon)
    // Assert
    composeTestRule.onNodeWithContentDescription(contentDescriptionMenuIcon).assertIsDisplayed()
  }

  @Test
  fun checksIfTheInitialPictureExistOnFavouriteScreenWhenItIsEmpty() {
    // Test screen is empty
    val contentDescriptionWeatherIcon = composeTestRule.activity.getString(R.string.favourite_content_description_weather_icon)
    val blank = composeTestRule.onNodeWithContentDescription(contentDescriptionWeatherIcon).isDisplayed()
    if (blank) {
      error("Screen is not empty")
//        Exception("Screen is not empty")
      }
    // Arrange
    val contentDescriptionInitialPicture =  composeTestRule.activity.getString(
      R.string.favourite_content_initial_picture
    )
    val textListOfFavouritesAreEmpty = composeTestRule.activity.getString(
      R.string.favourite_content_text_favourites_are_empty
    )
    val textAddFavouriteCitiesHere = composeTestRule.activity.getString(
      R.string.favourite_content_add_cities_here
    )

    // Assert
      composeTestRule.onNodeWithContentDescription(contentDescriptionInitialPicture).assertIsDisplayed()
      composeTestRule.onNodeWithText(textListOfFavouritesAreEmpty).assertIsDisplayed()
      composeTestRule.onNodeWithText(textAddFavouriteCitiesHere).assertIsDisplayed()
  }

  @Test
  fun setAndCheckSelectedCityAvailableOnFavouriteScreen() = runTest {
    // Arrange
    val button = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
    val contentDescriptionMenuIcon =
      composeTestRule.activity.getString(R.string.favourite_content_description_menu_icon)
    val contentDescriptionWeatherIcon = composeTestRule.activity.getString(R.string.favourite_content_description_weather_icon)
    // Act

    composeTestRule.onNodeWithText(
      composeTestRule.activity.getString(R.string.title_few_contents_text_favourite)
    ).performClick()

    composeTestRule.onNodeWithText(
      composeTestRule.activity.getString(R.string.search_content_text_into_placeholder)
    ).performTextInput("sofia")

    composeTestRule.onNode(button.and(hasContentDescription("Search button"))).performClick()

    composeTestRule.waitUntil (
      timeoutMillis = 5000,
      condition = {composeTestRule.onNodeWithText("Sofia").isDisplayed()}
      )

    composeTestRule.onNodeWithText("Grad Sofiya").performClick()

    composeTestRule.waitUntil (
      timeoutMillis = 5000,
      condition = {composeTestRule.onNodeWithContentDescription(contentDescriptionMenuIcon).isDisplayed()}
    )
    // Assert
    composeTestRule.onNodeWithContentDescription(contentDescriptionWeatherIcon).assertIsDisplayed()
    composeTestRule.onNodeWithText("Max: ").assertIsDisplayed()
    composeTestRule.onNodeWithText("Min: ").assertIsDisplayed()
  }
}