package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.LinearGradient
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE_16
import info.sergeikolinichenko.myapplication.utils.toPerCentFromFloat
import info.sergeikolinichenko.myapplication.utils.toUvToStringId

/** Created by Sergei Kolinichenko on 24.07.2024 at 21:17 (GMT+3) **/

internal const val MAXIMUM_UV = 11

@Composable
internal fun UvIndexAndCloudiness(
  modifier: Modifier = Modifier,
  uvIndex: Int,
  cloudCover: Float,
  precipitation: String?
) {

  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Max),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    WeatherConditionsBlock(
      modifier = Modifier.weight(1f),
      uvIndex = uvIndex,
      cloudiness = null,
      precipitation = null
    )
    WeatherConditionsBlock(
      modifier = Modifier.weight(1f),
      cloudiness = cloudCover.toPerCentFromFloat(),
      uvIndex = null,
      precipitation = precipitation,
    )
  }
}

@Composable
private fun WeatherConditionsBlock(
  modifier: Modifier = Modifier,
  uvIndex: Int? = null,
  cloudiness: String? = null,
  precipitation: String? = null
) {
  Card(
    modifier = modifier
      .fillMaxHeight()
      .clip(shape = MaterialTheme.shapes.medium),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.Start
    ) {

      Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {

        val iconId = if (uvIndex != null) R.drawable.sun_max
        else R.drawable.cloud

        val titleId = if (uvIndex != null) R.string.details_content_title_block_uv_index
        else R.string.details_content_title_block_cloudiness

        Icon(
          modifier = Modifier
            .size(TITLE_ICON_SIZE_16.dp),
          imageVector = ImageVector.vectorResource(id = iconId),
          tint = MaterialTheme.colorScheme.surfaceTint,
          contentDescription = stringResource(R.string.details_cintent_description_uv_index_icon)
        )
        Text(
          modifier = Modifier.padding(start = 8.dp),
          text = stringResource(titleId),
          style = MaterialTheme.typography.labelMedium,
          textAlign = TextAlign.Unspecified,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      Text(
        modifier = Modifier,
        text = uvIndex?.toString() ?: cloudiness ?: "",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Unspecified,
        color = MaterialTheme.colorScheme.onBackground
      )
      if (uvIndex != null) {
        Text(
          modifier = Modifier,
          text = stringResource(id = uvIndex.toUvToStringId()),
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Start,
          lineHeight = 13.sp,
          color = MaterialTheme.colorScheme.onBackground
        )
        LineOfUvIndex(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
          uvIndex = uvIndex
        )
      }
      if (precipitation != null) {
        Row(
          modifier = Modifier.padding(top = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Start
        ) {

          Icon(
            modifier = Modifier
              .size(TITLE_ICON_SIZE_16.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.horly_rain),
            tint = MaterialTheme.colorScheme.surfaceTint,
            contentDescription = stringResource(R.string.details_cintent_description_uv_index_icon)
          )
          Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.details_content_title_block_precipitation),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Unspecified,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
        Text(
          modifier = Modifier,
          text = precipitation,
          style = MaterialTheme.typography.headlineMedium,
          textAlign = TextAlign.Unspecified,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
    }
  }
}

@Composable
private fun LineOfUvIndex(
  modifier: Modifier = Modifier,
  uvIndex: Int = 0
) {

  val colorOutline = MaterialTheme.colorScheme.outline

  Canvas(
    modifier = modifier
      .height(6.dp)
  ) {

    val indexLength = (size.width / MAXIMUM_UV) * uvIndex

    drawLine(
      brush = Brush.horizontalGradient(colorStops = LinearGradient.gradientUvIndex),
      start = Offset(0f, 0f),
      end = Offset(size.width, 0f),
      strokeWidth = 4.dp.toPx()
    )
    drawCircle(
      color = Color.White,
      radius = 6.dp.toPx(),
      center = Offset(indexLength, 0f)
    )
    drawCircle(
      color = colorOutline,
      style = Stroke(width = 1.dp.toPx()),
      radius = 6.dp.toPx(),
      center = Offset(indexLength, 0f)
    )
  }
}