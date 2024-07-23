package info.sergeikolinichenko.myapplication.utils

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.sergeikolinichenko.myapplication.presentation.MainActivity
import org.junit.Rule
import org.junit.runner.RunWith

/** Created by Sergei Kolinichenko on 18.07.2024 at 21:38 (GMT+3) **/

@RunWith(AndroidJUnit4::class)
abstract class BaseUiTest {
  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()
}