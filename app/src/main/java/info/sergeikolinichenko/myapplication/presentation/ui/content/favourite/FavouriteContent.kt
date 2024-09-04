package info.sergeikolinichenko.myapplication.presentation.ui.content.favourite

import android.annotation.SuppressLint
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.component.FavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.store.FavouriteStore

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:06 (GMT+3) **/
@Composable
fun FavouriteContent(component: FavouriteComponent) {

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    AnimatedFavouriteContent(
      component = component,
      modifier = Modifier
        .fillMaxSize()
        .align(Alignment.TopCenter)
    )
  }
}

@Composable
internal fun FavoriteVerticalGrid(
  modifier: Modifier = Modifier,
  state: State<FavouriteStore.State>,
  columns: Int = NUMBER_OF_COLUMNS,
  lazyListState: LazyListState = rememberLazyListState(),
  onClickSearch: () -> Unit,
  onClickActionMenu: () -> Unit,
  onItemClicked: (Int) -> Unit,
  onDismissRequestDropdownMenu: () -> Unit,
  onClickSettings: () -> Unit,
  onClickEditing: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit
) {

  var swipeLeft by remember { mutableStateOf(false) }
  var swipeRight by remember { mutableStateOf(false) }

  LazyVerticalGrid(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
          if (dragAmount > 0) {
            swipeLeft = true
            swipeRight = false
          } else if (dragAmount < 0) {
            swipeRight = true
            swipeLeft = false
          }
          change.consume()
        }
      },
    columns = GridCells.Fixed(columns),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    cityGridContent(
      state = state,
      columns = columns,
      lazyListState = lazyListState,
      onClickSearch = { onClickSearch() },
      onClickActionMenu = { onClickActionMenu() },
      onItemClicked = { onItemClicked(it) },
      onDismissRequestDropdownMenu = { onDismissRequestDropdownMenu() },
      onClickSettings = { onClickSettings() },
      onClickEditing = { onClickEditing() }
    )
  }

  if (swipeLeft) onSwipeLeft()

  if (swipeRight) onSwipeRight()
}

private fun LazyGridScope.cityGridContent(
  state: State<FavouriteStore.State>,
  columns: Int,
  lazyListState: LazyListState,
  onClickSearch: () -> Unit,
  onClickActionMenu: () -> Unit,
  onItemClicked: (Int) -> Unit,
  onDismissRequestDropdownMenu: () -> Unit,
  onClickSettings: () -> Unit,
  onClickEditing: () -> Unit
) {

  item(
    span = { GridItemSpan(columns) }
  ) {
    SearchCard(
      onClickSearch = { onClickSearch() },
      state = state.value.dropDownMenuState,
      onClickActionMenu = { onClickActionMenu() },
      onDismissRequest = { onDismissRequestDropdownMenu() },
      onClickSettings = { onClickSettings() },
      onClickEditing = { onClickEditing() }
    )
  }

  when (val citiesState = state.value.citiesState) {

    FavouriteStore.State.CitiesState.Initial -> {
      item(
        span = { GridItemSpan(columns) }
      ) {
        InitialBox(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        )
      }
    }

    FavouriteStore.State.CitiesState.Error -> {

      item(
        span = { GridItemSpan(columns) }
      ) {
        InitialBox(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        )
      }
    }

    is FavouriteStore.State.CitiesState.Loaded -> {


      itemsIndexed(
        items = citiesState.listCities,
        key = { _, item -> item.id }
      ) { index, item ->
        val (delay, easing) = lazyListState.calculateDelayAndEasing(index, columns)
        val animation = tween<Float>(durationMillis = 500, delayMillis = delay, easing = easing)
        val args = ScaleAndAlphaArgs(fromScale = 2f, toScale = 1f, fromAlpha = 0f, toAlpha = 1f)
        val (scale, alpha) = scaleAndAlpha(args = args, animation = animation)

        CityCard(
          modifier = Modifier
            .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale),
          city = item,
          weatherState = state.value.weatherState,
          onItemClicked = { onItemClicked(item.id) }
        )
      }
    }

    FavouriteStore.State.CitiesState.Loading -> {
      item(
        span = { GridItemSpan(columns) }
      ) {
        CitiesLoadingBox(
          modifier = Modifier.fillMaxSize()
        )
      }
    }

  }
}

// Block for animation enter CityCard
@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
private fun LazyListState.calculateDelayAndEasing(index: Int, columnCount: Int): Pair<Int, Easing> {
  val row = index / columnCount
  val column = index % columnCount
  val firstVisibleRow = firstVisibleItemIndex
  val visibleRows = layoutInfo.visibleItemsInfo.count()
  val scrollingToBottom = firstVisibleRow < row
  val isFirstLoad = visibleRows == 0
  val rowDelay = 200 * when {
    isFirstLoad -> row // initial load
    scrollingToBottom -> visibleRows + firstVisibleRow - row // scrolling to bottom
    else -> 1 // scrolling to top
  }
  val scrollDirectionMultiplier = if (scrollingToBottom || isFirstLoad) 1 else -1
  val columnDelay = column * 150 * scrollDirectionMultiplier
  val easing = if (scrollingToBottom || isFirstLoad) LinearOutSlowInEasing else FastOutSlowInEasing
  return rowDelay + columnDelay to easing
}

@Composable
fun scaleAndAlpha(
  args: ScaleAndAlphaArgs,
  animation: FiniteAnimationSpec<Float>
): Pair<Float, Float> {
  val transitionState = remember {
    MutableTransitionState(TransitionState.PLACING).apply {
      targetState = TransitionState.PLACED
    }
  }
  val transition = updateTransition(transitionState, label = "")
  val alpha by transition.animateFloat(transitionSpec = { animation }, label = "") { state ->
    when (state) {
      TransitionState.PLACING -> args.fromAlpha
      TransitionState.PLACED -> args.toAlpha
    }
  }
  val scale by transition.animateFloat(transitionSpec = { animation }, label = "") { state ->
    when (state) {
      TransitionState.PLACING -> args.fromScale
      TransitionState.PLACED -> args.toScale
    }
  }
  return alpha to scale
}

@Composable
private fun InitialBox(
  modifier: Modifier = Modifier
) {

  Box(
    modifier = modifier,
  ) {

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {

      val img = if (isSystemInDarkTheme()) R.drawable.dark_initial_picture
      else R.drawable.light_initial_picture

      Icon(
        modifier = Modifier
          .size(300.dp),
        imageVector = ImageVector.vectorResource(id = img),
        contentDescription = stringResource(id = R.string.favourite_content_initial_picture),
        tint = Color.Unspecified,
      )
      Spacer(modifier = Modifier.size(20.dp))
      Text(
        text = stringResource(id = R.string.favourite_content_text_favourites_are_empty),
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.size(10.dp))
      Text(
        text = stringResource(id = R.string.favourite_content_add_cities_here),
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}

@Composable
private fun CitiesLoadingBox(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier,
  ) {
    CircularProgressIndicator(
      modifier = Modifier
        .size(48.dp)
        .align(Alignment.Center)
    )
  }
}

data class ScaleAndAlphaArgs(
  val fromScale: Float,
  val toScale: Float,
  val fromAlpha: Float,
  val toAlpha: Float
)

private enum class TransitionState { PLACING, PLACED }

private const val NUMBER_OF_COLUMNS = 1