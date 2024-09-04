package info.sergeikolinichenko.myapplication.presentation.ui.content.details.currentweather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore

/** Created by Sergei Kolinichenko on 19.08.2024 at 09:40 (GMT+3) **/

@Composable
internal fun AnimatedDetailsContent(
  modifier: Modifier = Modifier,
  component: DetailsComponent,
) {

  val state = component.model.collectAsState()

  val animState = remember { MutableTransitionState(false) }.apply {
    targetState = state.value.forecastState is DetailsStore.State.ForecastState.Loaded
  }

//  animState.targetState = state.value.forecastState is DetailsStore.State.ForecastState.Loaded

  AnimatedVisibility(
    visibleState = animState,
    enter = fadeIn(animationSpec = tween(300)) +
        scaleIn(animationSpec = tween(300),
          initialScale = 0.5f),

    exit = fadeOut(
      animationSpec = tween(300)
    ) + scaleOut(
      animationSpec = tween(300),
      targetScale = 0.5f),
  ) {
    DetailsScreen(
      modifier = modifier,
      state = state.value,
      onDayClicked = { id, index, forecast ->
        component.onDayClicked(id, index, forecast)
      },
      onSwipeLeft = {
        component.onSwipeLeft()
      },
      onSwipeRight = {
        component.onSwipeRight()
      },
      onBackClicked = {
        component.onBackClicked()
      },
      onSettingsClicked = {
        component.onSettingsClicked()
      }
    )
  }
}