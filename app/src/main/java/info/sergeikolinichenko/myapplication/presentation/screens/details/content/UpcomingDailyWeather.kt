package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
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
@Composable
private fun UpcomingDailyWeather(
  modifier: Modifier = Modifier,
  upcoming: List<DailyWeather>,
  gradient: Gradient
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    border = BorderStroke(1.dp, gradient.shadowColor),
    shape = MaterialTheme.shapes.extraLarge,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background.copy(
        alpha = 0.30f
      )
    )
  ) {
    Column(
      modifier = Modifier
        .padding(6.dp)
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
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        upcoming.forEach { weather ->
          WeatherDayItem(
            weather = weather,
            gradient = gradient
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RowScope.WeatherDayItem(
  modifier: Modifier = Modifier,
  weather: DailyWeather,
  gradient: Gradient
) {
  Card(
    modifier = modifier
      .fillMaxSize()
      .padding(6.dp)
      .sizeIn(
        minWidth = 80.dp,
        minHeight = 130.dp
      )
      .weight(1f),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.background.copy(
        alpha = 0.80f
      )
    ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceAround
    ) {

      Text(
        text = weather.date.toCalendar().formattedShortDayOfWeek(),
        style = MaterialTheme.typography.bodyLarge
      )

      HorizontalDivider(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp, horizontal = 6.dp),
        color = (gradient.shadowColor)
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Row(
          modifier = Modifier
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

        Row(modifier = Modifier
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

      if (weather.dailyWillTtRain == 1) {
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
      } else if (weather.dailyWillItSnow == 1) {
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
          text = stringResource(id = R.string.details_content_uv_index,
            weather.uv.toRoundToInt()),
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
          textStyle = MaterialTheme.typography.bodySmall
        )
      }
    }
  }
}

@Composable
fun AnimatedUpcomingDailyWeather(
  upcoming: List<DailyWeather>,
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
      gradient = gradient
    )
  }
}