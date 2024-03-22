package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.details.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardDarkGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardLightGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient
import info.sergeikolinichenko.myapplication.utils.formattedFullDate
import info.sergeikolinichenko.myapplication.utils.fromKphToStringId
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import info.sergeikolinichenko.myapplication.utils.toRoundToIntString
import info.sergeikolinichenko.myapplication.utils.toCloudyCover
import info.sergeikolinichenko.myapplication.utils.toListWeatherScreen
import info.sergeikolinichenko.myapplication.utils.toPrecipitation
import info.sergeikolinichenko.myapplication.utils.toPressure
import info.sergeikolinichenko.myapplication.utils.toWeatherScreen

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

        is DetailsStore.State.ForecastState.Loaded -> ForecastLoaded(
          forecast = forecast.forecast,
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
    title = { Text(text = cityName) },
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ForecastLoaded(
  modifier: Modifier = Modifier,
  forecast: Forecast,
  gradient: Gradient
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.weight(1f))

    Text(
      text = forecast.currentWeather.date.toCalendar().formattedFullDate(),
      style = MaterialTheme.typography.titleLarge,
    )

    HorizontalDivider(
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
    Text(
      text = forecast.currentWeather.descriptionWeather,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.W500
    )
    Row(
      modifier = Modifier.padding(vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val fontSizeTemperature = 100.sp
      Text(
        buildAnnotatedString {
          withStyle(SpanStyle(fontSize = fontSizeTemperature)) {
            append(forecast.currentWeather.temperature.toRoundToIntString())
          }
          withStyle(SpanStyle(fontSize = fontSizeTemperature / 2 )) {
            append("°C")
          }
        }
      )
      GlideImage(
        modifier = Modifier
          .size(100.dp),
        model = forecast.currentWeather.conditionUrl,
        contentDescription = stringResource(R.string.details_content_text_description_weather_condition)
      )
      DrawCompass(
        modifier = Modifier
          .size(100.dp)
          .padding(end = 6.dp),
        currentWeather = forecast.currentWeather.toWeatherScreen()
      )
    }
    DetailsCurrentWeather(forecast = forecast)
    Spacer(modifier = Modifier.weight(1f))
    WeatherCharts(
      modifier = Modifier
        .fillMaxWidth()
        .size(180.dp)
        .padding(vertical = 4.dp, horizontal = 8.dp),
      listWeather = forecast.upcomingHours.toListWeatherScreen(),
      gradient = gradient
    )
    Spacer(modifier = Modifier.weight(1f))
    AnimatedUpcomingHourlyWeather(upcoming = forecast.upcomingHours)
    Spacer(modifier = Modifier.weight(0.5f))
    AnimatedUpcomingDailyWeather(upcoming = forecast.upcomingDays)
    Spacer(modifier = Modifier.weight(0.5f))
  }
}
@Composable
private fun DetailsCurrentWeather(
  modifier: Modifier = Modifier,
  forecast: Forecast
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(4.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {

    Column(
      modifier = modifier.padding(start = 4.dp, end = 2.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          painter = painterResource(id = R.drawable.thermometer),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text =
          stringResource(
            R.string.details_content_title_feels,
            forecast.currentWeather.feelsLikeC.toCelsiusString()
          ),
          style = MaterialTheme.typography.bodySmall
        )
      }
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          painter = painterResource(id = R.drawable.precipitation),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_precipitation_mm,
            forecast.currentWeather.precipiceMm.toPrecipitation()
          ),
              style = MaterialTheme.typography.bodySmall)
      }
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          painter = painterResource(id = R.drawable.cloud_cover),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_cloudy,
            forecast.currentWeather.cloudCover.toCloudyCover()
          ),
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }
    Column(
      modifier = modifier.padding(start = 2.dp, end = 4.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          painter = painterResource(id = R.drawable.air_humidity),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_humidity,
            forecast.currentWeather.humidity
          ),
          style = MaterialTheme.typography.bodySmall,
        )
      }
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          painter = painterResource(id = R.drawable.atmospheric_pressure),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_atmospheric_pressure_mbar,
            forecast.currentWeather.airPressure.toPressure()
          ),
          style = MaterialTheme.typography.bodySmall,
        )
      }
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          painter = painterResource(id = R.drawable.wind_speed),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_wind_speed) +
              stringResource(id = forecast.currentWeather.windSpeed.fromKphToStringId()),
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }
  }
}
@Composable
private fun getGradient(numberGradient: Int): Gradient {
  val gradients = if (isSystemInDarkTheme()) CardDarkGradients.gradients
  else CardLightGradients.gradients

  return gradients[numberGradient]
}