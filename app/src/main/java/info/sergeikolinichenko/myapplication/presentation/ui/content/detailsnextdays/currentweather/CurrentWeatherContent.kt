package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.currentweather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.components.details.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.stores.details.DetailsStore

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:57 (GMT+3) **/

@Composable
fun CurrentWeatherContent(
  component: DetailsComponent
) {
  Box(
    modifier = Modifier
      .background(MaterialTheme.colorScheme.surfaceBright)
      .fillMaxSize()
  ) {

    val state by component.model.collectAsState()

    when (state.citiesState) {
      DetailsStore.State.CitiesState.LoadingFailed ->
        LoadingError(text = stringResource(R.string.details_content_error_loading_cities_text))

      DetailsStore.State.CitiesState.Initial -> Loading()

      is DetailsStore.State.CitiesState.Loaded -> {

        when (state.forecastState) {
          DetailsStore.State.ForecastsState.LoadingFailed ->
            LoadingError(text = stringResource(R.string.details_content_error_loading_forecast_text))

          DetailsStore.State.ForecastsState.Initial -> Loading()

          is DetailsStore.State.ForecastsState.Loaded -> {
            CurrentWeatherAnimation(
              state = state,
              onDayClicked = { id, index -> component.onDayClicked(id, index) },
              onBackClicked = { component.onBackClicked() },
              onSettingsClicked = { component.onSettingsClicked() },
              onSwipeRight = { component.onSwipeRight() },
              onSwipeLeft = { component.onSwipeLeft() }
            )
          }
        }
      }
    }
  }
}

@Composable
internal fun CurrentWeatherScreen(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onDayClicked: (Int, Int) -> Unit,
  onBackClicked: () -> Unit,
  onSettingsClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit
) {

  val city = (state.citiesState as DetailsStore.State.CitiesState.Loaded)
    .cities.first { it.id == state.citiesState.id }

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
private fun LoadingError(
  modifier: Modifier = Modifier,
  text: String
) {
  Box(
    modifier = modifier
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp)
      .fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      modifier = Modifier.padding(16.dp),
      text = text,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}