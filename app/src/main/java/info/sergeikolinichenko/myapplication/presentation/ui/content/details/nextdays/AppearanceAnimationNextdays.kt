package info.sergeikolinichenko.myapplication.presentation.ui.content.details.nextdays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import info.sergeikolinichenko.myapplication.presentation.screens.nextdays.store.NextdaysStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 14.08.2024 at 10:12 (GMT+3) **/

@Composable
internal fun AppearanceAnimationNextdays(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
  onDayClicked: (Int) -> Unit,
  onCloseClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit
) {

  val animState = remember { MutableTransitionState(false) }

  animState.targetState = state.forecast is NextdaysStore.State.ForecastState.Loaded

  var animatedDirection by remember { mutableStateOf(AnimatedDirection.Bottom) }

  AnimatedVisibility(
    visibleState = animState,
    enter = animatedDirection.directEnter(),
    exit = animatedDirection.directExit(),
  ) {

    NextdaysScreen(
      modifier = modifier,
      state = state,
      onDayClicked = {
        CoroutineScope(Dispatchers.Main).launch {
          animState.targetState = false
          delay(350)
          onDayClicked(it)
        }
      },
      onCloseClicked = { onCloseClicked() },
      onSwipeLeft = {
        animState.targetState = false
        onSwipeLeft() },
      onSwipeRight = {
        animState.targetState = false
        onSwipeRight() },
      animatedDirection = {
        animatedDirection = it
        animState.targetState = true
      }
    )
  }
}

private fun AnimatedDirection.directEnter() =
  when(this){
    AnimatedDirection.Top -> {
      fadeIn(animationSpec = tween(300)) +
          slideIn(animationSpec = tween(300),
            initialOffset = { IntOffset(0, it.height) })
    }
    AnimatedDirection.Bottom -> {
      fadeIn(animationSpec = tween(300)) +
          slideIn(animationSpec = tween(300),
            initialOffset = { IntOffset(0, -it.height) })
    }

    AnimatedDirection.Left -> {
      fadeIn(animationSpec = tween(300)) +
          slideIn(animationSpec = tween(300),
            initialOffset = { IntOffset(it.width, 0) })
    }
    AnimatedDirection.Right -> {
      fadeIn(animationSpec = tween(300)) +
          slideIn(animationSpec = tween(300),
            initialOffset = { IntOffset(-it.width, 0) })
    }
  }

private fun AnimatedDirection.directExit() =
  when (this) {
    AnimatedDirection.Top -> {
      fadeOut(
        animationSpec = tween(300)
      ) + slideOut(
        animationSpec = tween(300),
        targetOffset = { IntOffset(0, -it.height) })
    }
    AnimatedDirection.Bottom -> {
      fadeOut(
        animationSpec = tween(300)
      ) + slideOut(
        animationSpec = tween(300),
        targetOffset = { IntOffset(0, it.height) })
    }

    AnimatedDirection.Left -> {
      fadeOut(
        animationSpec = tween(300)
      ) + slideOut(
        animationSpec = tween(300),
        targetOffset = { IntOffset(-it.width, 0) })
    }
    AnimatedDirection.Right -> {
      fadeOut(
        animationSpec = tween(300)
      ) + slideOut(
        animationSpec = tween(300),
        targetOffset = { IntOffset(it.width, 0) })
    }
  }

internal enum class AnimatedDirection {
  Top, Bottom, Left, Right
}