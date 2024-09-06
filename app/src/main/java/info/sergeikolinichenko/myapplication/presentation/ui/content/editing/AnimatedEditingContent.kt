package info.sergeikolinichenko.myapplication.presentation.ui.content.editing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import info.sergeikolinichenko.myapplication.presentation.screens.editing.component.EditingComponent
import info.sergeikolinichenko.myapplication.presentation.screens.editing.store.EditingStore

/** Created by Sergei Kolinichenko on 18.08.2024 at 19:11 (GMT+3) **/

@Composable
internal fun AnimatedEditingContent(
  component: EditingComponent,
  modifier: Modifier = Modifier
) {

  val state by component.model.collectAsState()

  val animState = remember { MutableTransitionState(false) }
  animState.targetState = state.cities is EditingStore.State.CitiesStatus.CitiesLoaded

  AnimatedVisibility(
    modifier = modifier,
    visibleState = animState,
    enter = fadeIn(animationSpec = tween(300)) +
        slideIn(animationSpec = tween(300),
          initialOffset = { IntOffset(-it.width, 0) }),
    exit = fadeOut(animationSpec = tween(300)) +
        slideOut(animationSpec = tween(300),
          targetOffset = { IntOffset(-it.width, 0) }),
  ) {

    EditingScreen(
      state = state,
      onCloseClicked = { component.onBackClicked() },
      onSwipeRight = {component.onBackClicked()},
      onDoneClicked = { component.onDoneClicked() },
      changedListCities = { component.listOfCitiesChanged(it) }
    )
  }

}