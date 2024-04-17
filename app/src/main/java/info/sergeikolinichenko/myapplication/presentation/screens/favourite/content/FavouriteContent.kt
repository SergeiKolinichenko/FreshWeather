package info.sergeikolinichenko.myapplication.presentation.screens.favourite.content

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.CityScreen
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.FavouriteComponent
import info.sergeikolinichenko.myapplication.presentation.screens.favourite.FavouriteStore
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.getGradient
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import kotlin.random.Random

/** Created by Sergei Kolinichenko on 21.02.2024 at 16:06 (GMT+3) **/
@Composable
fun FavouriteContent(component: FavouriteComponent) {

  val state = component.model.collectAsState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    FavoriteVerticalGrid(
      modifier = Modifier.align(Alignment.TopCenter),
      state = state,
      onClickSearch = { component.onSearchClicked() },
      onClickToCity = { city, numberGradient ->
        component.onItemClicked(city = city, numberGradient = numberGradient)
      }
    )

    FavouriteFloatingActionButton(
      modifier = Modifier.align(Alignment.BottomCenter),
      state = state,
      onClickFloatingActionButton = { component.onButtonClicked() }
    )

  }
}

@Composable
private fun FavoriteVerticalGrid(
  modifier: Modifier = Modifier,
  state: State<FavouriteStore.State>,
  columns: Int = 2,
  lazyListState: LazyListState = rememberLazyListState(),
  onClickSearch: () -> Unit,
  onClickToCity: (CityScreen, Int) -> Unit
) {
  LazyVerticalGrid(
    modifier = modifier.fillMaxSize(),
    columns = GridCells.Fixed(columns),
    contentPadding = PaddingValues(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    content = {
      cityGridContent(
        state = state,
        columns = columns,
        lazyListState = lazyListState,
        onClickSearch = { onClickSearch() },
        onClickToCity = { city, gradient ->
          onClickToCity(city, gradient)
        }
      )
    }
  )
}

private fun LazyGridScope.cityGridContent(
  state: State<FavouriteStore.State>,
  columns: Int,
  lazyListState: LazyListState,
  onClickSearch: () -> Unit,
  onClickToCity: (CityScreen, Int) -> Unit
) {

  item(
    span = { GridItemSpan(columns) }
  ) {
    SearchCard(
      onClickSearch = {
        onClickSearch()
      }
    )
  }
  itemsIndexed(
    items = state.value.cityItems,
    key = { _, item -> item.city.id }
  ) { index, item ->
    val (delay, easing) = lazyListState.calculateDelayAndEasing(index, columns)
    val animation = tween<Float>(durationMillis = 500, delayMillis = delay, easing = easing)
    val args = ScaleAndAlphaArgs(fromScale = 2f, toScale = 1f, fromAlpha = 0f, toAlpha = 1f)
    val (scale, alpha) = scaleAndAlpha(args = args, animation = animation)
    val numberGradient = index % 5
    CityCard(
      modifier = Modifier.graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale),
      item = item,
      numberGradient = numberGradient,
      onClickToCity = { onClickToCity(item.city, numberGradient) }
    )
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CityCard(
  modifier: Modifier = Modifier,
  item: FavouriteStore.State.CityItem,
  numberGradient: Int = 0,
  onClickToCity: () -> Unit
) {

  val gradient = getGradient(numberGradient)

  Card(
    modifier = modifier
      .shadow(
        elevation = 16.dp,
        spotColor = gradient.shadowColor,
        shape = MaterialTheme.shapes.extraLarge
      ),
    shape = MaterialTheme.shapes.extraLarge
  ) {
    Box(
      modifier = modifier
        .sizeIn(minHeight = 196.dp, minWidth = 100.dp)
        .background(gradient.primaryGradient)
        .drawBehind {
          drawCircle(
            brush = gradient.secondaryGradient,
            center = Offset(
              x = center.x - size.width / 15,
              y = center.y + size.height / 3
            ),
            radius = size.maxDimension / 2
          )
        }
        .padding(12.dp)
        .clickable { onClickToCity() }
    ) {

      Column(
        modifier = Modifier
          .fillMaxSize()
          .align(Alignment.BottomStart),
        verticalArrangement = Arrangement.SpaceEvenly
      ) {

        when (val weatherState = item.weatherState) {
          FavouriteStore.State.WeatherState.Error -> {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(R.string.favourite_content_error_weather_for_city),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W600)
              )
            }
          }

          FavouriteStore.State.WeatherState.Initial -> {}

          is FavouriteStore.State.WeatherState.LoadedWeather -> {

            GlideImage(
              modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(100.dp),
              model = weatherState.iconUrl,
              contentDescription = stringResource(R.string.content_icon_description_weather_icon)
            )

            Row(
              modifier = Modifier.align(Alignment.Start),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.thermometer),
                contentDescription = "Temperature"
              )
              Text(
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.W600),
                text = weatherState.temperature.toCelsiusString()
              )
            }
          }

          FavouriteStore.State.WeatherState.Loading -> {
            CircularProgressIndicator(
              modifier = Modifier
                .align(Alignment.CenterHorizontally)
            )
          }

        }
        ResponsiveText(
          text = item.city.name,
          modifier = Modifier.align(Alignment.Start),
          textStyle = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground
        )
        ResponsiveText(
          text = item.city.country,
          modifier = Modifier.align(Alignment.Start),
          textStyle = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
    }
  }
}

@Composable
private fun SearchCard(
  modifier: Modifier = Modifier,
  onClickSearch: () -> Unit
) {
  val gradient = getGradient(Random.nextInt(0, 5))
  Card(
    modifier = modifier
      .fillMaxWidth(),
    shape = CircleShape,
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .background(gradient.primaryGradient)
        .clickable { onClickSearch() },
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 8.dp),
        imageVector = Icons.Default.Search,
        contentDescription = "Search"
      )
      Text(
        modifier = Modifier.padding(end = 16.dp),
        text = stringResource(R.string.favourite_content_text_search)
      )
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

data class ScaleAndAlphaArgs(
  val fromScale: Float,
  val toScale: Float,
  val fromAlpha: Float,
  val toAlpha: Float
)

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

private enum class TransitionState { PLACING, PLACED }