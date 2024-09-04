package info.sergeikolinichenko.myapplication.presentation.ui.content.details.currentweather

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.ForecastFs
import info.sergeikolinichenko.myapplication.entity.HourForecastFs
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.AnimatingHourlyWeatherForecast
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.Charts
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.HumidityWindPressure
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.SunAndMoon
import info.sergeikolinichenko.myapplication.presentation.ui.content.details.UvIndexAndCloudiness
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:57 (GMT+3) **/

@Composable
fun DetailsContent(
  component: DetailsComponent
) {
  AnimatedDetailsContent(
    modifier = Modifier,
    component = component
  )
}

@Composable
internal fun DetailsScreen(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onDayClicked: (Int, Int, ForecastFs) -> Unit,
  onBackClicked: () -> Unit,
  onSettingsClicked: () -> Unit,
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit
) {
  if (state.citiesState is DetailsStore.State.CitiesState.Loaded) {

    val city = state.citiesState.cities.first { it.id == state.citiesState.id }

    when (val forecastState = state.forecastState) {
      DetailsStore.State.ForecastState.Error -> Error()
      DetailsStore.State.ForecastState.Initial -> Initial()
      DetailsStore.State.ForecastState.Loading -> Loading()

      is DetailsStore.State.ForecastState.Loaded -> {
        MainScreen(
          modifier = modifier,
          city = city,
          forecast = forecastState.forecast,
          state = state,
          onDayClicked = {
            onDayClicked(city.id, it, forecastState.forecast)
          },
          onBackClicked = { onBackClicked() },
          onSettingsClicked = { onSettingsClicked() },
          onSwipeLeft = { onSwipeLeft() },
          onSwipeRight = { onSwipeRight() }
        )
      }
    }
  }
}


@Composable
private fun Initial() {
  Loading()
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