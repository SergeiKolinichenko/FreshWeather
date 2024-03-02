package info.sergeikolinichenko.myapplication.presentation.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardGradients
import info.sergeikolinichenko.myapplication.utils.formattedFullDate
import info.sergeikolinichenko.myapplication.utils.formattedShortDayOfWeek
import info.sergeikolinichenko.myapplication.utils.toCelsius
import kotlin.random.Random

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:57 (GMT+3) **/

@Composable
fun DetailsContent(component: DetailsComponent) {

  val state by component.model.collectAsState()

  Scaffold(
    containerColor = Color.Transparent,
    contentColor = MaterialTheme.colorScheme.background,
    modifier = Modifier
      .fillMaxSize()
      .background(CardGradients.gradients[Random.nextInt(0, 5)].primaryGradient),
    topBar = {
      TopBar(
        cityName = state.city.name,
        isCityFavourite = state.isFavourite,
        onBack = { component.onBackClicked() },
        onChangeFavouriteStatus = { component.onChangeFavouriteStatusClicked() }
      )
    }
  ) { padding ->
    Box(modifier = Modifier.padding(padding)) {
      when (val forecast = state.forecastState) {
        DetailsStore.State.ForecastState.Error -> Error()

        DetailsStore.State.ForecastState.Initial -> Initial()

        is DetailsStore.State.ForecastState.Loaded -> ForecastLoaded( forecast = forecast.forecast)

        DetailsStore.State.ForecastState.Loading -> Loading()
      }
    }
  }
}
@Composable
private fun Initial() {
  // TODO()
}
@Composable
private fun Error() {
  // TODO()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
  cityName: String,
  isCityFavourite: Boolean,
  onBack: () -> Unit,
  onChangeFavouriteStatus: () -> Unit,
) {
  CenterAlignedTopAppBar(
    title = { Text(text = cityName) },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = Color.Transparent,
      titleContentColor = MaterialTheme.colorScheme.background,
    ),
    navigationIcon = {
      IconButton(onClick = { onBack() }) {
        Icon(
          imageVector = Icons.Default.ArrowBackIosNew,
          contentDescription = stringResource(R.string.details_content_text_description_button_back),
          tint = MaterialTheme.colorScheme.background
        )
      }
    },
    actions = {
      IconButton(onClick = { onChangeFavouriteStatus() }) {
        val icon = if (isCityFavourite) {
          Icons.Default.Star
        } else {
          Icons.Default.StarBorder
        }
        Icon(
          imageVector = icon,
          contentDescription = stringResource(R.string.details_content_text_description_button_favourite),
          tint = MaterialTheme.colorScheme.background
        )
      }
    }
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
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ForecastLoaded(
  modifier: Modifier = Modifier,
  forecast: Forecast
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.weight(1f))

    Text(
      text = forecast.currentWeather.descriptionWeather,
      style = MaterialTheme.typography.titleLarge,
    )
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = forecast.currentWeather.temperature.toCelsius(),
        style = MaterialTheme.typography.headlineLarge.copy(
          fontSize = 70.sp
        ),
      )
      GlideImage(
        modifier = Modifier
          .padding(start = 14.dp)
          .size(80.dp),
        model = forecast.currentWeather.conditionUrl,
        contentDescription = stringResource(R.string.details_content_text_description_weather_condition)
      )
    }
    Text(
      text = forecast.currentWeather.date.formattedFullDate(),
      style = MaterialTheme.typography.titleLarge,
    )

    Spacer(modifier = Modifier.weight(1f))

    AnimatedUpcomingWeather(upcoming = forecast.upcoming)

    Spacer(modifier = Modifier.weight(0.5f))
  }
}
@Composable
private fun UpcomingWeather(
  modifier: Modifier = Modifier,
  upcoming: List<Weather>
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(24.dp),
    shape = MaterialTheme.shapes.extraLarge,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background.copy(
        alpha = 0.30f
      )
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        modifier = Modifier
          .padding(bottom = 24.dp)
          .align(Alignment.CenterHorizontally),
        text = stringResource(R.string.details_content_title_block_upcoming_weather),
        style = MaterialTheme.typography.headlineMedium,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        upcoming.forEach { weather ->
          WeatherItem(weather = weather)
        }
      }
    }
  }
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RowScope.WeatherItem(
  modifier: Modifier = Modifier,
  weather: Weather
) {
  Card(
    modifier = modifier
      .size(130.dp)
      .weight(1f),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background
    ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = weather.temperature.toCelsius())
      GlideImage(
        modifier = Modifier.size(50.dp),
        model = weather.conditionUrl,
        contentDescription = stringResource(R.string.content_icon_description_weather_icon)
      )
      Text(text = weather.date.formattedShortDayOfWeek())
    }
  }
}
@Composable
private fun AnimatedUpcomingWeather(upcoming: List<Weather>) {

  val state = remember {
    MutableTransitionState(false).apply { targetState = true }
  }
  AnimatedVisibility(
    visibleState = state,
    enter = fadeIn(animationSpec = tween(500))
        + slideIn(animationSpec = tween(500),
          initialOffset = { IntOffset(0, it.height) }),
  ) {
    UpcomingWeather(upcoming = upcoming)
  }
}