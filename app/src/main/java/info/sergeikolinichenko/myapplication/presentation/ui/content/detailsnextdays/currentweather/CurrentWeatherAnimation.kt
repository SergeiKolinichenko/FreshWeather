package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.currentweather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStore
import info.sergeikolinichenko.myapplication.utils.DURATION_OF_ANIMATION

/** Created by Sergei Kolinichenko on 25.09.2024 at 13:28 (GMT+3) **/

@Composable
internal fun CurrentWeatherAnimation(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onDayClicked: (Int, Int) -> Unit,
  onBackClicked: () -> Unit,
  onSettingsClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit
) {

  AnimatedContent(
    targetState = state,
    transitionSpec = {

      val initialCityIndex = (initialState.citiesState as DetailsStore.State.CitiesState.Loaded)
        .cities.indexOfFirst { it.id == (initialState.citiesState as DetailsStore.State.CitiesState.Loaded).id }

      val targetCityIndex =
        (targetState.citiesState as DetailsStore.State.CitiesState.Loaded).cities
          .indexOfFirst { it.id == (targetState.citiesState as DetailsStore.State.CitiesState.Loaded).id }

      if (initialCityIndex < targetCityIndex) {

        (fadeIn(animationSpec = tween(DURATION_OF_ANIMATION))
            + slideInHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
          initialOffsetX = { fullWidth -> fullWidth }))
          .togetherWith(
            fadeOut(animationSpec = tween(DURATION_OF_ANIMATION))
                + slideOutHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
              targetOffsetX = { fullWidth -> -fullWidth })
          )

      } else if (initialCityIndex > targetCityIndex) {

        (fadeIn(animationSpec = tween(DURATION_OF_ANIMATION))
            + slideInHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
          initialOffsetX = { fullWidth -> -fullWidth }))
          .togetherWith(
            fadeOut(animationSpec = tween(DURATION_OF_ANIMATION))
                + slideOutHorizontally(animationSpec = tween(DURATION_OF_ANIMATION),
              targetOffsetX = { fullWidth -> fullWidth })
          )

      } else {
        ContentTransform(EnterTransition.None, ExitTransition.None)
      }
    },
    label = "CurrentWeatherAnimation"
  )
  { targetState ->

    CurrentWeatherScreen(
      modifier = modifier,
      state = targetState,
      onDayClicked = onDayClicked,
      onBackClicked = onBackClicked,
      onSettingsClicked = onSettingsClicked,
      onSwipeLeft = onSwipeLeft,
      onSwipeRight = onSwipeRight
    )
  }
}