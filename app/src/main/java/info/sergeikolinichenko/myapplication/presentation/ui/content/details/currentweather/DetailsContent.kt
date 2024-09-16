package info.sergeikolinichenko.myapplication.presentation.ui.content.details.currentweather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:57 (GMT+3) **/

@Composable
fun DetailsContent(
  component: DetailsComponent
) {
  Box(
    modifier = Modifier
      .background(MaterialTheme.colorScheme.surfaceBright)
      .fillMaxSize()
  ) {
    AnimatedDetailsContent(
      modifier = Modifier,
      component = component
    )
  }
}

@Composable
private fun AnimatedDetailsContent(
  modifier: Modifier = Modifier,
  component: DetailsComponent,
) {

  val state = component.model.collectAsState()

  val animState = remember { MutableTransitionState(false) }.apply {
    targetState = state.value.forecastState is DetailsStore.State.ForecastsState.Loaded
  }

//  animState.targetState = state.value.forecastState is DetailsStore.State.ForecastsState.Success

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
      onDayClicked = { id, index ->
        component.onDayClicked(id, index)
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

@Composable
private fun DetailsScreen(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onDayClicked: (Int, Int) -> Unit,
  onBackClicked: () -> Unit,
  onSettingsClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit
) {
  if (state.citiesState is DetailsStore.State.CitiesState.Loaded) {

    val city = state.citiesState.cities.first { it.id == state.citiesState.id }

    when (state.forecastState) {
      DetailsStore.State.ForecastsState.Failed -> Error()
      DetailsStore.State.ForecastsState.Loading -> Loading()

      is DetailsStore.State.ForecastsState.Loaded -> {

          MainScreen(
            modifier = modifier,
            state = state,
            onDayClicked = { dayNumber -> onDayClicked(city.id, dayNumber) },
            onBackClicked = { onBackClicked() },
            onSettingsClicked = { onSettingsClicked() },
            onSwipeLeft = { onSwipeLeft() },
            onSwipeRight = { onSwipeRight() }
          )
      }

      DetailsStore.State.ForecastsState.Initial -> {}
    }
  }
}


@Composable
private fun Loading() {
  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    CircularProgressIndicator(
      modifier = Modifier.align(Alignment.Center),
      color = MaterialTheme.colorScheme.background
    )
  }
}

@Composable
private fun Error() {
  Box(
    modifier = Modifier
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp)
      .fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      modifier = Modifier.padding(16.dp),
      text = stringResource(R.string.favourite_content_error_weather_for_city),
      style = MaterialTheme.typography.bodyLarge
    )
  }
}