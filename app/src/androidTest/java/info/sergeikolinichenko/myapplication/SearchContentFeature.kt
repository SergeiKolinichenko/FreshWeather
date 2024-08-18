package info.sergeikolinichenko.myapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import info.sergeikolinichenko.myapplication.presentation.ui.content.search.TEST_SEARCH_TEXT_TAG
import info.sergeikolinichenko.myapplication.utils.BaseUiTest
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** Created by Sergei Kolinichenko on 23.07.2024 at 10:28 (GMT+3) **/

class SearchContentFeature : BaseUiTest() {

  @Test
  fun checkIfSearchScreenIsOpen() {
    with(composeTestRule) {
      // Arrange
      // Act
      onNodeWithText(activity.getString(R.string.title_few_contents_text_favourite)).performClick()
      // Assert
      onNodeWithText(activity.getString(R.string.search_content_text_into_placeholder)).assertIsDisplayed()
    }
  }

  @Test
  fun checkIfMessageIsDisplayedIfThereAreLessThan_3_LettersInSearchString() {
    with(composeTestRule) {
      // Arrange
      onNodeWithText(
        activity.getString(R.string.title_few_contents_text_favourite)
      ).performClick()
      // Act
      onNodeWithText(
        activity.getString(R.string.search_content_text_into_placeholder)
      ).performTextInput("me")
      // Assert
      onNodeWithText(
        activity.getString(R.string.search_content_message_enter_more_than_3_letters_to_start_the_search)
      ).assertIsDisplayed()
    }
  }

  @Test
  fun checkFfMessageIsDisplayedIfNothingIsFound() = runTest {
    with(composeTestRule) {
      // Arrange
      onNodeWithText(
        activity.getString(R.string.title_few_contents_text_favourite)
      ).performClick()
      // Act
      onNodeWithText(
        activity.getString(R.string.search_content_text_into_placeholder)
      ).performTextInput("mezdu")
      waitUntil(
        timeoutMillis = 1000,
        condition = {
          onNodeWithText(
            activity.getString(R.string.search_content_message_text_nothing_found_on_your_request)
          ).isDisplayed()
        }
      )
      // Assert
      onNodeWithText(
        activity.getString(R.string.search_content_message_text_nothing_found_on_your_request)
      ).assertIsDisplayed()
    }
  }

  @Test
  fun checkDisplayOfFoundCity() = runTest{
    with(composeTestRule) {
      // Arrange
      onNodeWithText(
        activity.getString(R.string.title_few_contents_text_favourite)
      ).performClick()
      // Act
      onNodeWithText(
        activity.getString(R.string.search_content_text_into_placeholder)
      ).performTextInput("sofia")
      waitUntil(
        timeoutMillis = 5000,
        condition = { onAllNodesWithTag(TEST_SEARCH_TEXT_TAG).onFirst().isDisplayed() }
      )
      // Assert
      onNodeWithText("Sofia, Grad Sofiya, Bulgaria").assertIsDisplayed()
    }
  }

  @Test
  fun checkIfFoundAndSelectedCityIsSavedToFavourites() = runTest{
    with(composeTestRule) {
      // Arrange
      onNodeWithText(
        activity.getString(R.string.title_few_contents_text_favourite)
      ).performClick()
      // Act
      onNodeWithText(
        activity.getString(R.string.search_content_text_into_placeholder)
      ).performTextInput("sofia")
      waitUntil(
        timeoutMillis = 5000,
        condition = { onAllNodesWithTag(TEST_SEARCH_TEXT_TAG).onFirst().isDisplayed() }
      )
      onAllNodesWithText("Sofia, Grad Sofiya, Bulgaria").onFirst().performClick()
      waitUntil(
        timeoutMillis = 5000,
        condition = { onAllNodesWithText("Max: ").onFirst().isDisplayed() }
      )
       // Assert
      onNodeWithText("Sofia").assertIsDisplayed()
    }
  }
}
