package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.details.component.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.store.DetailsStore

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:57 (GMT+3) **/

@Composable
fun AnimatedDetailsContent(
  modifier: Modifier = Modifier,
  component: DetailsComponent
) {

  val state = remember {
    MutableTransitionState(false).apply { targetState = true }
  }

  AnimatedVisibility(
    visibleState = state,
    enter = fadeIn(animationSpec = tween(300, 1000)) +
        slideIn(animationSpec = tween( 300, 1000),
      initialOffset = { IntOffset(it.width, 0) }),

    exit = fadeOut(
      animationSpec = tween(300))
        + slideOut(
      animationSpec = tween(300),
        targetOffset = { IntOffset(-it.width, 0) }),
  ) {
    DetailsScreen(
      modifier = modifier,
      component = component,
    )
  }
}

@Composable
fun DetailsContent(component: DetailsComponent) {

  Box( // it's for background
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    AnimatedDetailsContent(
      modifier = Modifier,
      component = component
    )
  }
}

@Composable
private fun DetailsScreen(
  modifier: Modifier = Modifier,
  component: DetailsComponent
) {

  val state = component.model.collectAsState()

  Column(
    modifier = modifier
      .padding(bottom = 16.dp)
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {

    TopBar(
      modifier = Modifier.padding(
        start = 16.dp,
        top = 20.dp,
        end = 16.dp,
        bottom = 36.dp
      ),
      state = state.value,
      onBackButtonClick = {
        component.onBackClicked()
      },
      onChangeFavouriteStatusClicked = {
        component.onChangeFavouriteStatusClicked()
      }
    )

    MainScreen(
      modifier = Modifier, state = state.value)
  }
}

@Composable
private fun TopBar(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
  onBackButtonClick: () -> Unit,
  onChangeFavouriteStatusClicked: () -> Unit
) {

  Box(
    modifier = modifier
      .fillMaxWidth(),
  ) {
    // Back button
    Icon(
      modifier = Modifier
        .size(24.dp)
        .align(Alignment.CenterStart)
        .clickable { onBackButtonClick() },
      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = stringResource(R.string.details_content_description_text_back_button)
    )

    if (state.citiesState is DetailsStore.State.CitiesState.Loaded) {


      if (state.forecastState is DetailsStore.State.ForecastState.Loaded) {
        Row(
          modifier = modifier.align(Alignment.Center),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (number in state.citiesState.cities) {
            if (number.id == state.citiesState.id) {
              Icon(
                modifier = Modifier.size(8.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
                tint = Color.Black,
                contentDescription = null
              )
            } else {
              Icon(
                modifier = Modifier.size(8.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ellipse),
                tint = Color.White,
                contentDescription = null
              )
            }
          }
        }
      }
    }



    Text(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .clickable { onChangeFavouriteStatusClicked() },
      text = when (state.isFavourite) {
        true -> stringResource(R.string.details_content_button_delete_from_favourite)
        false -> stringResource(R.string.details_content_button_add_to_favourite)
      },
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      fontSize = 16.sp,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
private fun MainScreen(
  modifier: Modifier = Modifier,
  state: DetailsStore.State,
) {
  if (state.citiesState is DetailsStore.State.CitiesState.Loaded) {

    val city = state.citiesState.cities.first { it.id == state.citiesState.id }

    when (val forecastState = state.forecastState) {
      DetailsStore.State.ForecastState.Error -> Error()
      DetailsStore.State.ForecastState.Initial -> Initial()
      DetailsStore.State.ForecastState.Loading -> Loading()

      is DetailsStore.State.ForecastState.Loaded -> {

        val forecast = forecastState.forecast

        CurrentWeather(
          modifier = Modifier
            .padding(
              start = 16.dp,
              end = 16.dp
            ),
          forecast = forecast,
          city = city
        )

        CurrentWeatherConditions(
          modifier = Modifier
            .padding(
              top = 24.dp,
              start = 16.dp,
              end = 16.dp
            ),
          forecast = forecast
        )

        AnimatingHourlyWeatherForecast(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          forecast = forecast
        )

        DailyWeatherForecast(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          forecast = forecast
        )

        UvIndexAndCloudiness(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          forecast = forecast
        )
        SunAndMoon(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          forecast = forecast
        )
        Charts(
          modifier = Modifier
            .padding(
              top = 16.dp,
              start = 16.dp,
              end = 16.dp
            ),
          forecast = forecast
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
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      modifier = Modifier.padding(16.dp),
      text = stringResource(R.string.favourite_content_error_weather_for_city),
      style = MaterialTheme.typography.bodyLarge
    )
  }
}