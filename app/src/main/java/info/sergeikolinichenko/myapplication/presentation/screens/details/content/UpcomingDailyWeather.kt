package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.DailyWeather
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.formattedShortDayOfWeek
import info.sergeikolinichenko.myapplication.utils.fromKphToStringId
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import info.sergeikolinichenko.myapplication.utils.toRoundToInt

/** Created by Sergei Kolinichenko on 22.03.2024 at 19:55 (GMT+3) **/
private const val TRUE = 1

@Composable
private fun UpcomingDailyWeather(
  modifier: Modifier = Modifier,
  upcoming: List<DailyWeather>,
  timeZone: String,
  gradient: Gradient
) {
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
        text = stringResource(
          R.string.details_content_title_block_upcoming_weather
        ),
        textStyle = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground
      )
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        items(
          items = upcoming,
          key = { it.date }
        ) {
          WeatherDayItem(
            weather = it,
            gradient = gradient,
            timeZone = timeZone
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun WeatherDayItem(
  modifier: Modifier = Modifier,
  weather: DailyWeather,
  timeZone: String,
  gradient: Gradient
) {
  Column(
    modifier = modifier
      .sizeIn(
        maxHeight = 260.dp,
        maxWidth = 150.dp
      )
      .padding(start = 4.dp, end = 4.dp, bottom = 6.dp)
      .border(
        BorderStroke(1.dp, gradient.tertiaryGradient),
        shape = RoundedCornerShape(8.dp)
      ),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(),
      colors = CardDefaults.cardColors(
        containerColor = gradient.shadowColor
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
          .padding(vertical = 2.dp, horizontal = 10.dp),
        text = weather.date.toCalendar(timeZone).formattedShortDayOfWeek(),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
      )
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Row(
        modifier = Modifier
          .padding(start = 4.dp, end = 4.dp, top = 4.dp)
          .align(Alignment.CenterStart),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
      ) {

        Icon(
          modifier = Modifier.size(14.dp),
          painter = painterResource(id = R.drawable.temperature_arrow_down),
          contentDescription = "Temperature min"
        )
        Text(
          text = weather.minTempC.toCelsiusString(),
          style = MaterialTheme.typography.bodySmall
        )
      }

      Row(
        modifier = Modifier
          .align(Alignment.CenterEnd),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        Icon(
          modifier = Modifier.size(14.dp),
          painter = painterResource(id = R.drawable.temperature_arrow_up),
          contentDescription = "Temperature max"
        )
        Text(
          text = weather.maxTempC.toCelsiusString(),
          style = MaterialTheme.typography.bodySmall
        )
      }
    }

    GlideImage(
      modifier = Modifier.size(100.dp),
      model = weather.condIconUrl,
      contentDescription = stringResource(R.string.content_icon_description_weather_icon)
    )

    if (weather.dailyWillTtRain == TRUE) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(18.dp),
          painter = painterResource(id = R.drawable.rain),
          contentDescription = "Chance of rain"
        )
        Spacer(modifier = Modifier.size(2.dp))
        Text(
          text = stringResource(
            R.string.details_content_chance_of_rain,
            weather.dailyChanceOfRain, "%"
          ),
          style = MaterialTheme.typography.bodySmall
        )
      }
    } else if (weather.dailyWillItSnow == TRUE) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(18.dp),
          painter = painterResource(id = R.drawable.snow),
          contentDescription = "Chance of snow"
        )
        Spacer(modifier = Modifier.size(2.dp))
        Text(
          text = stringResource(
            R.string.details_content_chance_of_snow,
            weather.dailyChanceOfSnow, "%"
          ),
          style = MaterialTheme.typography.bodySmall
        )
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        modifier = Modifier.size(18.dp),
        painter = painterResource(id = R.drawable.uv_index),
        contentDescription = "Chance of snow"
      )
      Spacer(modifier = Modifier.size(2.dp))
      Text(
        text = stringResource(
          id = R.string.details_content_index,
          weather.uv.toRoundToInt()
        ),
        style = MaterialTheme.typography.bodySmall
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        modifier = Modifier.size(18.dp),
        painter = painterResource(id = R.drawable.wind),
        contentDescription = "Wind speed"
      )
      Spacer(modifier = Modifier.size(2.dp))
      ResponsiveText(
        text = stringResource(id = weather.windKph.fromKphToStringId()),
        textStyle = MaterialTheme.typography.bodySmall,
        maxLines = 2
      )
    }
  }
}

@Composable
fun AnimatedUpcomingDailyWeather(
  upcoming: List<DailyWeather>,
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
      initialOffset = { IntOffset(0, it.height) }),
  ) {
    UpcomingDailyWeather(
      upcoming = upcoming,
      gradient = gradient,
      timeZone = timeZone
    )
  }
}