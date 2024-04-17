package info.sergeikolinichenko.myapplication.presentation.screens.favourite.content

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.FavouriteStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/** Created by Sergei Kolinichenko on 23.03.2024 at 17:17 (GMT+3) **/

private const val TRIGGER_HEIGHT = -30
@Composable
fun FavouriteFloatingActionButton(
  modifier: Modifier = Modifier,
  state: State<FavouriteStore.State>,
  onClickFloatingActionButton: () -> Unit
) {

  var stateEfab by rememberEfabState()
  
  val scope = rememberCoroutineScope()
  LaunchedEffect(key1 = null) {
    scope.launch {
      delay(500)
      val isListItemsEmpty = state.value.cityItems.isEmpty()
      if (isListItemsEmpty) stateEfab = stateEfab.copy(isTransparent = true, isTop = true)
    }
  }

  ArrowUp(
    modifier = modifier,
    state = stateEfab,
    dragAmount = {
      if (!stateEfab.isTransparent) {
        stateEfab = stateEfab.copy(offsetY = stateEfab.offsetY + it)
      }
    },
    isChange = {
      stateEfab = stateEfab.copy(isTransparent = true, isTop = true)
    }
  )
  AnimatedEfab(
    modifier = modifier,
    state = stateEfab,
    isChange = { stateEfab = stateEfab.copy(isTop = false, isTransparent = false) },
    onClickEfab = { onClickFloatingActionButton() }
  )
}

@Composable
private fun ArrowUp(
  modifier: Modifier = Modifier,
  state: EfabState,
  dragAmount: (Float) -> Unit,
  isChange: () -> Unit
) {

  val alpha by animateFloatAsState( targetValue =
  if (state.isTransparent) 0f
  else 1f, label = ""
  )

  Icon(
    modifier = modifier
      .size(40.dp)
      .alpha(alpha)
      .clickable { isChange() }
      .draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState {
          dragAmount(it)
          if (it >= TRIGGER_HEIGHT) isChange()
        }
      ),
    imageVector = Icons.Default.KeyboardArrowUp,
    contentDescription = null
  )
}
@Composable
private fun AnimatedEfab(
  modifier: Modifier = Modifier,
  state: EfabState,
  isChange: () -> Unit,
  onClickEfab: () -> Unit
) {

  AnimatedContent(
    modifier = modifier.padding(bottom = 16.dp),
    targetState = state.isTop, label = "",
    transitionSpec = {
      if (targetState) {
        slideInVertically { it } togetherWith slideOutVertically { -it }
      } else {
        slideInVertically { -it } togetherWith slideOutVertically { +it }
      }

    }
  ) {
    if (it) {
      Efab(
        isChange = { isChange() },
        onClickEfab = { onClickEfab()}
      )
    }
  }
}
@Composable
private fun Efab(
  modifier: Modifier = Modifier,
  isChange: () -> Unit,
  onClickEfab: () -> Unit
) {

    ExtendedFloatingActionButton(
      modifier = modifier
        .draggable(
          orientation = Orientation.Vertical,
          state = rememberDraggableState {
            if (it > 0) isChange()
          }
        ),
      onClick = { onClickEfab() },
      icon = {
        Icon(
          Icons.Filled.Add,
          null
        )
      },
      text = {
        Text(
          text = stringResource(R.string.ext_floating_action_button_title),
          style = MaterialTheme.typography.titleMedium
        )
      },
    )
}

@Parcelize
private data class EfabState(
  val offsetY: Float = 0f,
  val isTransparent: Boolean = false,
  val isTop: Boolean = false
  ) : Parcelable

@Composable
private fun rememberEfabState():
    MutableState<EfabState> =
  remember { mutableStateOf(EfabState()) }