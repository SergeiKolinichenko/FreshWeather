package info.sergeikolinichenko.myapplication.presentation.ui.content.detailsnextdays.nextdaysforecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.HourForecastFs
import info.sergeikolinichenko.myapplication.presentation.components.nextdays.NextdaysComponent
import info.sergeikolinichenko.myapplication.presentation.stors.nextdaysforecast.NextdaysStore
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Created by Sergei Kolinichenko on 11.08.2024 at 17:10 (GMT+3) **/

@Composable
fun NextdaysContent(
  component: NextdaysComponent
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surfaceBright),
  ) {

    val state by component.model.collectAsState()

    TopBar(
      modifier = Modifier,
      state = component.model.collectAsState().value
    )

    when (state.citiesState) {
      NextdaysStore.State.CitiesState.Error -> ErrorScreen(
        text = stringResource(R.string.error_loading_favourite_cities_please_inform_the_developers_about_the_problem)
      )

      NextdaysStore.State.CitiesState.Initial -> LoadingScreen()
      is NextdaysStore.State.CitiesState.Loaded -> {



        when (state.forecastState) {
          NextdaysStore.State.ForecastState.Error -> ErrorScreen(
            text = stringResource(R.string.error_receiving_weather_forecast_please_try_again_later_if_the_error_occurs_again_please_notify_the_developers)
          )
          NextdaysStore.State.ForecastState.Initial -> LoadingScreen()
          is NextdaysStore.State.ForecastState.Loaded -> {

            NextdaysAnimation(
              modifier = Modifier.weight(1f),
              state = state,
              onDayClicked = { component.onDayClicked(it) },
              onCloseClicked = { component.onCloseClicked() },
              onSwipeLeft = { component.onSwipeLeft() },
              onSwipeRight = { component.onSwipeRight() },
              onSwipeTop = { component.onSwipeTop() },
              onSwipeBottom = { component.onSwipeBottom() }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ErrorScreen(
  modifier: Modifier = Modifier,
  text: String
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Text(
      modifier = Modifier.align(Alignment.Center),
      text = text,
      style = MaterialTheme.typography.titleLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
private fun LoadingScreen(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    CircularProgressIndicator(
      modifier = Modifier.align(Alignment.Center)
    )
  }
}

@Composable
private fun TopBar(
  modifier: Modifier = Modifier,
  state: NextdaysStore.State,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 20.dp),
  ) {
    Row(
      modifier = modifier
        .align(Alignment.Center),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (state.citiesState is NextdaysStore.State.CitiesState.Loaded) {
        for (number in state.citiesState.cities) {
          if (number.id == state.citiesState.id) {

            Icon(
              modifier = Modifier
                .size(8.dp),
              imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
              tint = Color.Black,
              contentDescription = null
            )
          } else {
            Icon(
              modifier = Modifier
                .size(8.dp),
              imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
              tint = Color.White,
              contentDescription = null
            )
          }
        }
      }
    }
  }
}

fun getHoursForDay(hours: List<HourForecastFs>, dayEpoch: Long, tz: String): List<HourForecastFs> {
  val zoneId = ZoneId.of(tz)
  val dayStart = Instant.ofEpochSecond(dayEpoch).atZone(zoneId).toLocalDate().atStartOfDay(zoneId)

  val dayEnd = dayStart.plus(1, ChronoUnit.DAYS)

  return hours.filter { hour ->
    val hourInstant = Instant.ofEpochSecond(hour.date).atZone(zoneId)
    hourInstant.isAfter(dayStart) && hourInstant.isBefore(dayEnd)
  }
}
