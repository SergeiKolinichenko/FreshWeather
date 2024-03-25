package info.sergeikolinichenko.myapplication.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardDarkGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardLightGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient

/** Created by Sergei Kolinichenko on 23.03.2024 at 11:00 (GMT+3) **/
@Composable
fun getGradient(numberGradient: Int): Gradient {
  val gradients = if (isSystemInDarkTheme()) CardDarkGradients.gradients
  else CardLightGradients.gradients

  return gradients[numberGradient]
}