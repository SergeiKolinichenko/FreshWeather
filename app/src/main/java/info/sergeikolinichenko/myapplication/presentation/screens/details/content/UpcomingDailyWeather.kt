package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.formattedShortDayOfWeek
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toRoundToIntString

/** Created by Sergei Kolinichenko on 22.03.2024 at 19:55 (GMT+3) **/
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
        text = "max: ${weather.maxTempC?.toRoundToIntString()}",
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = "min: ${weather.minTempC?.toRoundToIntString()}",
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
@Composable
fun AnimatedUpcomingDailyWeather(upcoming: List<Weather>) {

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