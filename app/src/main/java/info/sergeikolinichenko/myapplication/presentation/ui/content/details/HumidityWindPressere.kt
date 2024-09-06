package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE_16
import info.sergeikolinichenko.myapplication.utils.fromKphToStringId
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import info.sergeikolinichenko.myapplication.utils.toWindDirection

/** Created by Sergei Kolinichenko on 27.07.2024 at 16:00 (GMT+3) **/

@Composable
internal fun HumidityWindPressure(
  modifier: Modifier = Modifier,
  humidity: Float,
  windDir: Float,
  windSpeed: Float,
  pressure: String
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
      value = humidity.toPerCentFromFloat()
    )
    // Wind block
    WeatherConditionsBlock(
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically),
      icon = ImageVector.vectorResource(id = R.drawable.compass),
      textToWindBlock = stringResource(id = windDir.toWindDirection()),
      title = stringResource(R.string.details_content_conditions_text_wind),
      value = stringResource(id = windSpeed.fromKphToStringId())
    )

    // Pressure block
    WeatherConditionsBlock(
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically),
      icon = ImageVector.vectorResource(id = R.drawable.barometer),
      title = stringResource(R.string.details_content_conditions_text_pressure),
      value = pressure
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
    modifier = modifier
      .fillMaxHeight(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Row(
        modifier = Modifier
          .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier
            .padding(end = 4.dp)
            .size(TITLE_ICON_SIZE_16.dp),
          imageVector = icon,
          tint = MaterialTheme.colorScheme.surfaceTint,
          contentDescription = stringResource(R.string.details_content_description_current_weather_conditions_block_icon)
        )

        ResponsiveText(
          text = textToWindBlock,
          textStyle = MaterialTheme.typography.labelMedium,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1
        )
      }
      ResponsiveText(
        text = title,
        textStyle = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1
        )

      Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {

        val textValue = value.substringBeforeLast("\n")

        ResponsiveText(
          text = textValue,
          textStyle = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center,
          lineHeight = MaterialTheme.typography.bodyMedium.fontSize,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1
        )

        if (title == stringResource(R.string.details_content_conditions_text_pressure)) {

          val textDescription = value.substringAfterLast("\n")

          ResponsiveText(
            text = textDescription,
            textStyle = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            lineHeight = 10.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
          )
        }
      }
    }
  }
}