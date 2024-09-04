package info.sergeikolinichenko.myapplication.presentation.ui.content.favourite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.component.FavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.nextdaysforecast.AnimatedDirection

/** Created by Sergei Kolinichenko on 18.08.2024 at 15:29 (GMT+3) **/

@Composable
internal fun AnimatedFavouriteContent(
  component: FavouriteComponent,
  modifier: Modifier = Modifier
) {

  val state = component.model.collectAsState()
  val animState = remember { MutableTransitionState(false) }

  val stateForTarget = state.value.citiesState is FavouriteStore.State.CitiesState.Loaded ||
      state.value.citiesState is FavouriteStore.State.CitiesState.Error

  animState.targetState = stateForTarget
  var animatedDirection by remember { mutableStateOf(AnimatedDirection.Top) }

  AnimatedVisibility(
    visibleState = animState,
    enter = animatedDirection.directEnter(),
    exit = animatedDirection.directExit(),
  ) {

    FavoriteVerticalGrid(
      modifier = modifier,
      state = state,
      onClickSearch = { component.onSearchClicked() },
      onClickActionMenu = { component.onActionMenuClicked() },
      onItemClicked = { component.onItemClicked(it) },
      onDismissRequestDropdownMenu = { component.onClosingActionMenu() },
      onClickSettings = { component.onItemMenuSettingsClicked() },
      onClickEditing = { component.onItemMenuEditingClicked() },
      onSwipeLeft = {
        animatedDirection = AnimatedDirection.Left
        animState.targetState = false
        component.onItemMenuEditingClicked()
      },
      onSwipeRight = {
        if (state.value.citiesState is FavouriteStore.State.CitiesState.Loaded) {
          animatedDirection = AnimatedDirection.Right
          animState.targetState = false
          val id =
            (state.value.citiesState as FavouriteStore.State.CitiesState.Loaded).listCities.first().id
          component.onItemClicked(id)
        }
      }
    )
  }
}

private fun AnimatedDirection.directEnter() =
  when (this) {
    AnimatedDirection.Top -> {
      fadeIn(animationSpec = tween(0)) +
          scaleIn(animationSpec = tween(0))
    }

    AnimatedDirection.Bottom -> {
      fadeIn(animationSpec = tween(0)) +
          scaleIn(animationSpec = tween(0))
    }

    AnimatedDirection.Left -> {
      fadeIn(animationSpec = tween(DURATION_ANIMATION_SWIPE_HORIZONTAL)) +
          slideIn(animationSpec = tween(DURATION_ANIMATION_SWIPE_HORIZONTAL),
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
      fadeOut(animationSpec = tween(0)) +
          scaleOut(animationSpec = tween(0))
    }

    AnimatedDirection.Bottom -> {
      fadeOut(animationSpec = tween(0)) +
          scaleOut(animationSpec = tween(0))
    }

    AnimatedDirection.Left -> {
      fadeOut(
        animationSpec = tween(DURATION_ANIMATION_SWIPE_HORIZONTAL)
      ) + slideOut(
        animationSpec = tween(DURATION_ANIMATION_SWIPE_HORIZONTAL),
        targetOffset = { IntOffset(it.width, 0) })
    }

    AnimatedDirection.Right -> {
      fadeOut(
        animationSpec = tween(DURATION_ANIMATION_SWIPE_HORIZONTAL)
      ) + slideOut(
        animationSpec = tween(DURATION_ANIMATION_SWIPE_HORIZONTAL),
        targetOffset = { IntOffset(it.width, 0) })
    }
  }

private const val DURATION_ANIMATION_SWIPE_HORIZONTAL = 300