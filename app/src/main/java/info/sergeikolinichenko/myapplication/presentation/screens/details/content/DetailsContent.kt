package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.details.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardDarkGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardLightGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:57 (GMT+3) **/
@Composable
fun DetailsContent(component: DetailsComponent) {

  val state by component.model.collectAsState()
  val gradient = getGradient(numberGradient = state.numberGradient)

  Scaffold(
    containerColor = Color.Transparent,
    contentColor = MaterialTheme.colorScheme.onBackground,
    modifier = Modifier
      .fillMaxSize()
      .background(gradient.primaryGradient),
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

        is DetailsStore.State.ForecastState.Loaded -> DetailsForecast(
          forecast = forecast.forecast,
          timeZone = state.city.idTimeZone,
          gradient = gradient
        )

        DetailsStore.State.ForecastState.Loading -> Loading()
      }
    }
  }
}

@Composable
private fun Initial() {
  Loading()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
  cityName: String,
  isCityFavourite: Boolean,
  onBack: () -> Unit,
  onChangeFavouriteStatus: () -> Unit,
) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = cityName,
        fontWeight = FontWeight.W600
      ) },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = Color.Transparent,
      titleContentColor = MaterialTheme.colorScheme.onBackground,
    ),
    navigationIcon = {
      IconButton(onClick = { onBack() }) {
        Icon(
          imageVector = Icons.Default.ArrowBackIosNew,
          contentDescription = stringResource(R.string.details_content_text_description_button_back),
          tint = MaterialTheme.colorScheme.onBackground
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
          tint = MaterialTheme.colorScheme.onBackground
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
@Composable
private fun getGradient(numberGradient: Int): Gradient {
  val gradients = if (isSystemInDarkTheme()) CardDarkGradients.gradients
  else CardLightGradients.gradients

  return gradients[numberGradient]
}