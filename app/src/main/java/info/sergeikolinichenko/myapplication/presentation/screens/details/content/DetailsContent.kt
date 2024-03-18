package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.screens.details.DetailsComponent
import info.sergeikolinichenko.myapplication.presentation.screens.details.DetailsStore
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardDarkGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.CardLightGradients
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.formattedFullDate
import info.sergeikolinichenko.myapplication.utils.formattedHourAtDay
import info.sergeikolinichenko.myapplication.utils.formattedShortDayOfWeek
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toCelsius
import info.sergeikolinichenko.myapplication.utils.toListWeatherScreen
import java.util.Calendar

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
      text = stringResource(R.string.favourtite_content_error_weather_for_city),
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
    UpcomingHourlyWeather(upcoming = forecast.upcomingHours)
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
  Column(
    modifier = modifier.padding(start = 20.dp),
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
        style = MaterialTheme.typography.titleSmall,
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
          R.string.deatails_content_wind_speed_km_h,
          forecast.currentWeather.windSpeed
        ),
        style = MaterialTheme.typography.titleSmall,
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
          R.string.deatails_content_atmospheric_pressure_mbar,
          forecast.currentWeather.airPressure
        ),
        style = MaterialTheme.typography.titleSmall,
      )
    }
  }
}
@Composable
private fun UpcomingHourlyWeather(
  modifier: Modifier = Modifier,
  upcoming: List<Weather>
) {
  val currentDate = Calendar.getInstance()
  val nextHours = upcoming.filter { it.date.toCalendar() > currentDate }
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(4.dp),
    shape = MaterialTheme.shapes.extraLarge,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background.copy(
        alpha = 0.30f
      )
    )
  ) {
    Column(
      modifier = Modifier.padding(4.dp)
    ) {
      ResponsiveText(
        modifier = Modifier
          .align(Alignment.CenterHorizontally),
        text = stringResource(R.string.details_content_title_block_upcoming_hourly_weather),
        textStyle = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground
      )
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(
          items = nextHours,
          key = { it.date }
        ) {
          WeatherHourItem(weather = it)
        }
      }
    }
  }
}
@Composable
private fun UpcomingDailyWeather(
  modifier: Modifier = Modifier,
  upcoming: List<Weather>
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    shape = MaterialTheme.shapes.extraLarge,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background.copy(
        alpha = 0.30f
      )
    )
  ) {
    Column(
      modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
      ResponsiveText(
        modifier = Modifier
          .align(Alignment.CenterHorizontally),
        text = stringResource(R.string.details_content_title_block_upcoming_weather),
        textStyle = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        upcoming.forEach { weather ->
          WeatherDayItem(weather = weather)
        }
      }
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RowScope.WeatherDayItem(
  modifier: Modifier = Modifier,
  weather: Weather
) {
  Card(
    modifier = modifier
      .sizeIn(minWidth = 100.dp, maxWidth = 150.dp, minHeight = 130.dp, maxHeight = 200.dp)
      .weight(1f),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background
    ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "max: ${weather.maxTemp?.toCelsius()}",
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = "min: ${weather.minTemp?.toCelsius()}",
        style = MaterialTheme.typography.bodyLarge
      )
      GlideImage(
        modifier = Modifier.size(70.dp),
        model = weather.conditionUrl,
        contentDescription = stringResource(R.string.content_icon_description_weather_icon)
      )
      Text(
        text = weather.date.toCalendar().formattedShortDayOfWeek(),
        style = MaterialTheme.typography.bodyLarge
      )
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun WeatherHourItem(
  modifier: Modifier = Modifier,
  weather: Weather
) {
  Card(
    modifier = modifier
      .sizeIn(
        minWidth = 60.dp,
        maxWidth = 80.dp,
        minHeight = 70.dp,
        maxHeight = 124.dp
      ),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = weather.date.toCalendar().formattedHourAtDay(),
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W500)
      )
      HorizontalDivider(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
      )
      GlideImage(
        modifier = Modifier.size(48.dp),
        model = weather.conditionUrl,
        contentDescription = stringResource(R.string.content_icon_description_weather_icon)
      )
      Text(
        text = weather.temperature.toCelsius(),
        style = MaterialTheme.typography.bodySmall
      )
      Text(
        text = "${weather.humidity}%",
        style = MaterialTheme.typography.bodySmall
      )
    }
  }
}

@Composable
private fun AnimatedUpcomingDailyWeather(upcoming: List<Weather>) {

  val state = remember {
    MutableTransitionState(false).apply { targetState = true }
  }
  AnimatedVisibility(
    visibleState = state,
    enter = fadeIn(animationSpec = tween(500))
        + slideIn(animationSpec = tween(500),
      initialOffset = { IntOffset(0, it.height) }),
  ) {
    UpcomingDailyWeather(upcoming = upcoming)
  }
}

@Composable
private fun getGradient(numberGradient: Int): Gradient {
  val gradients = if (isSystemInDarkTheme()) CardDarkGradients.gradients
  else CardLightGradients.gradients

  return gradients[numberGradient]
}