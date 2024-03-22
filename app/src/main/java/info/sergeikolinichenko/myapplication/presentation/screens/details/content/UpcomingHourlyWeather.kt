package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.Weather
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.formattedHourAtDay
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import java.util.Calendar

/** Created by Sergei Kolinichenko on 22.03.2024 at 19:16 (GMT+3) **/
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
      Row {
        Icon(
          modifier = Modifier.size(16.dp),
          painter = painterResource(id = R.drawable.thermometer),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = weather.temperature.toCelsiusString(),
          style = MaterialTheme.typography.bodySmall
        )
      }

      Row {
        Icon(
          painter = painterResource(id = R.drawable.wind_speed),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = weather.windSpeed.toString(),
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }
  }
}
@Composable
fun AnimatedUpcomingHourlyWeather(upcoming: List<Weather>) {

  val state = remember {
    MutableTransitionState(false).apply { targetState = true }
  }
  AnimatedVisibility(
    visibleState = state,
    enter = fadeIn(animationSpec = tween(500))
        + slideIn(animationSpec = tween(500),
      initialOffset = { IntOffset(it.width, 0) }),
  ) {
    UpcomingHourlyWeather(upcoming = upcoming)
  }
}