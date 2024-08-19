package info.sergeikolinichenko.myapplication.presentation.ui.content.details.currentweather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore

/** Created by Sergei Kolinichenko on 19.08.2024 at 09:40 (GMT+3) **/

@Composable
internal fun AnimatedDetailsContent(
  modifier: Modifier = Modifier,
  component: DetailsComponent
) {

  val state = component.model.collectAsState()

  val animState = remember { MutableTransitionState(false) }

  animState.targetState = state.value.forecastState is DetailsStore.State.ForecastState.Loaded

  AnimatedVisibility(
    visibleState = animState,
    enter = fadeIn(animationSpec = tween(300)) +
        slideIn(animationSpec = tween(300),
          initialOffset = { IntOffset(it.width, 0) }),

    exit = fadeOut(
      animationSpec = tween(300)
    ) + slideOut(
      animationSpec = tween(300),
      targetOffset = { IntOffset(-it.width, 0) }),
  ) {
    DetailsScreen(
      modifier = modifier,
      state = state,
      onBackClicked = { component.onBackClicked() },
      onChangeFavouriteStatusClicked = { component.onSettingsClicked() },
      onDayClicked = { id, index, forecast ->
        component.onDayClicked(
          id = id,
          index = index,
          forecast = forecast
        )
      }
    )
  }
}