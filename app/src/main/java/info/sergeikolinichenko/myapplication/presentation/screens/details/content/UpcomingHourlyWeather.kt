package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.HourlyWeather
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.formattedFullHour
import info.sergeikolinichenko.myapplication.utils.formattedOnlyMonth
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import info.sergeikolinichenko.myapplication.utils.toPerCent
import java.util.Calendar

/** Created by Sergei Kolinichenko on 22.03.2024 at 19:16 (GMT+3) **/
private const val TRUE = 1
@Composable
private fun UpcomingHourlyWeather(
  modifier: Modifier = Modifier,
  upcoming: List<HourlyWeather>,
  timeZone: String,
  gradient: Gradient
) {
  val currentDate = Calendar.getInstance()
  val nextHours = upcoming.filter { it.date.toCalendar(timeZone) > currentDate }
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    border = BorderStroke(1.dp, gradient.shadowColor),
    shape = RoundedCornerShape(
      topStart = 0.dp,
      topEnd = 0.dp,
      bottomStart = 0.dp,
      bottomEnd = 0.dp
    ),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.30f)
    )
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = 6.dp)
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
        horizontalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        items(
          items = nextHours,
          key = { it.date }
        ) {
          WeatherHourItem(
            weather = it,
            timeZone = timeZone,
            gradient = gradient
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun WeatherHourItem(
  modifier: Modifier = Modifier,
  weather: HourlyWeather,
  timeZone: String,
  gradient: Gradient
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(start = 4.dp, end = 4.dp, bottom = 6.dp)
      .border(
        BorderStroke(1.dp, gradient.tertiaryGradient),
        shape = RoundedCornerShape(8.dp)
      ),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(0.dp)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.CenterHorizontally),
      colors = CardDefaults.cardColors(
        containerColor = gradient.shadowColor,
      ),
      shape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
      )
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp, start = 8.dp, end = 8.dp),
        text = weather.date.toCalendar(timeZone).formattedOnlyMonth(),
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W500),
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline
      )
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 2.dp, start = 6.dp, end = 6.dp),
        text = weather.date.toCalendar(timeZone).formattedFullHour(),
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W500),
        textAlign = TextAlign.Center
      )
    }

    GlideImage(
      modifier = Modifier.size(48.dp),
      model = weather.condIconUrl,
      contentDescription = stringResource(R.string.content_icon_description_weather_icon)
    )
    Row {
      Icon(
        modifier = Modifier.size(SIZE_DETAILS_ICONS),
        painter = painterResource(id = R.drawable.thermometer),
        contentDescription = null
      )
      Spacer(modifier = Modifier.padding(2.dp))
      Text(
        text = weather.tempC.toCelsiusString(),
        style = MaterialTheme.typography.bodySmall
      )
    }

    Row {
      Icon(
        modifier = Modifier.size(SIZE_DETAILS_ICONS),
        painter = painterResource(id = R.drawable.wind),
        contentDescription = null
      )
      Spacer(modifier = Modifier.padding(2.dp))
      Text(
        text = weather.windKph.toString(),
        style = MaterialTheme.typography.bodySmall,
      )
    }

    if (weather.willItRain == TRUE) {
      Row {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.rain),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
          text = weather.chanceOfRain.toPerCent(),
          style = MaterialTheme.typography.bodySmall,
        )
      }
    } else if (weather.willItSnow == TRUE) {
      Row {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.snow),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
          text = weather.chanceOfSnow.toPerCent(),
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }
  }
}

@Composable
fun AnimatedUpcomingHourlyWeather(
  upcoming: List<HourlyWeather>,
  timeZone: String,
  gradient: Gradient
) {

  val state = remember {
    MutableTransitionState(false).apply { targetState = true }
  }
  AnimatedVisibility(
    visibleState = state,
    enter = fadeIn(animationSpec = tween(500))
        + slideIn(animationSpec = tween(500),
      initialOffset = { IntOffset(it.width, 0) }),
  ) {
    UpcomingHourlyWeather(
      upcoming = upcoming,
      timeZone = timeZone,
      gradient = gradient
    )
  }
}