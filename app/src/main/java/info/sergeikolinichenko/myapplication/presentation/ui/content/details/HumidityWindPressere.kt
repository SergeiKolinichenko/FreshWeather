package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE
import info.sergeikolinichenko.myapplication.utils.fromKphToStringId
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import info.sergeikolinichenko.myapplication.utils.toWindDirection

/** Created by Sergei Kolinichenko on 27.07.2024 at 16:00 (GMT+3) **/

@Composable
internal fun CurrentWeatherConditions(
  modifier: Modifier = Modifier,
  forecast: Forecast
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Max),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {

    // Humidity block

    WeatherConditionsBlock(
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically),
      icon = ImageVector.vectorResource(id = R.drawable.humidity),
      title = stringResource(R.string.details_content_conditions_text_humidity),
      value = forecast.currentForecast.humidity.toPerCentFromFloat()
    )
    // Wind block
    WeatherConditionsBlock(
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically),
      icon = ImageVector.vectorResource(id = R.drawable.compass),
      textToWindBlock = stringResource(id = forecast.currentForecast.windDir.toWindDirection()),
      title = stringResource(R.string.details_content_conditions_text_wind),
      value = stringResource(id = forecast.currentForecast.windSpeed.fromKphToStringId())
    )

    // Pressure block
    WeatherConditionsBlock(
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically),
      icon = ImageVector.vectorResource(id = R.drawable.thermostat),
      title = stringResource(R.string.details_content_conditions_text_pressure),
      value = forecast.currentForecast.pressure
    )
  }
}

@Composable
private fun WeatherConditionsBlock(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  textToWindBlock: String = "",
  title: String,
  value: String,
) {
  Card(
    modifier = modifier.fillMaxHeight(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Row(
        modifier = Modifier.padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier
            .padding(end = 4.dp)
            .size(TITLE_ICON_SIZE.dp),
          imageVector = icon,
          tint = MaterialTheme.colorScheme.surfaceTint,
          contentDescription = stringResource(R.string.details_content_description_current_weather_conditions_block_icon)
        )
        Text(
          text = textToWindBlock,
          style = MaterialTheme.typography.labelSmall,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = title,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = value,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}