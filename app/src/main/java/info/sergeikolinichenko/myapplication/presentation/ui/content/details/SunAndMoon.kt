package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE_16
import info.sergeikolinichenko.myapplication.utils.WEATHER_ICON_SIZE_36
import info.sergeikolinichenko.myapplication.utils.durationBetweenTwoTimes
import info.sergeikolinichenko.myapplication.utils.getTime
import info.sergeikolinichenko.myapplication.utils.isTodayOrTomorrow
import info.sergeikolinichenko.myapplication.utils.toPhaseOfMoonStringId

/** Created by Sergei Kolinichenko on 26.07.2024 at 10:15 (GMT+3) **/

@Composable
internal fun SunAndMoon(
  modifier: Modifier = Modifier,
  sunrise: Long,
  sunset: Long,
  moonrise: Long,
  moonset: Long,
  moonPhase: Float,
  tzId: String
) {

  val timeSunrise = getTime(sunrise, tzId)

  val timeSunset = getTime(sunset, tzId)

  val timeMoonrise = getTime(moonrise, tzId)

  val timeMoonset = getTime(moonset, tzId)
  val sunInSky = durationBetweenTwoTimes(sunrise, sunset, LocalContext.current)

  val moonInSky = durationBetweenTwoTimes(moonrise, moonset, LocalContext.current)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(270.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier
            .size(TITLE_ICON_SIZE_16.dp),
          imageVector = ImageVector.vectorResource(id = R.drawable.sun_moon),
          contentDescription = stringResource(R.string.details_content_description_sun_and_moon_icon)
        )
        Text(
          modifier = Modifier.padding(start = 4.dp),
          text = stringResource(R.string.details_content_title_card_sun_and_moon),
          style = MaterialTheme.typography.labelMedium,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {

        val sunGradient = listOf(Color(0xFFFBD645), Color.Transparent)
        val moonGradient = listOf(Color.White, Color.Transparent)

        Item(
          modifier = Modifier,
          gradient = sunGradient,
          itemAlpha = 0.35f,
          duration = sunInSky,
          rise = timeSunrise,
          set = timeSunset,
          riseIcon = R.drawable.sunrise,
          setIcon = R.drawable.sunset,
          tittleRise = stringResource(R.string.details_content_tittle_sun_moon_block_today),
          tittleSet = stringResource(R.string.details_content_tittle_sun_moon_block_today)
        )
        Item(
          modifier = Modifier,
          gradient = moonGradient,
          itemAlpha = 0.70f,
          duration = moonInSky,
          rise = timeMoonrise,
          set = timeMoonset,
          riseIcon = R.drawable.moonrise,
          setIcon = R.drawable.moonset,
          tittleRise = stringResource(isTodayOrTomorrow(moonset, tzId)),
          tittleSet = stringResource(R.string.details_content_tittle_sun_moon_block_tomorrow)
        )
      }
    }

    Spacer(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(MaterialTheme.colorScheme.surfaceBright)
    )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {

        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(shape = MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceBright)
        )
        {
          Icon(
            modifier = Modifier
              .size(TITLE_ICON_SIZE_16.dp)
              .align(Alignment.Center),
            imageVector = ImageVector.vectorResource(id = R.drawable.moon_phases),
            tint = Color.Unspecified,
            contentDescription = null
          )
        }
        Column(
          modifier = Modifier
            .fillMaxHeight(),
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.SpaceAround
        ) {
          Text(
            text = stringResource(id = moonPhase.toPhaseOfMoonStringId()),
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Text(
            text = stringResource(R.string.details_content_tittle_sun_moon_block_tomorrow),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = 12.sp
          )
        }
      }
  }
}

@Composable
private fun Item(
  modifier: Modifier = Modifier,
  gradient: List<Color>,
  itemAlpha: Float,
  duration: String,
  rise: String,
  set: String,
  riseIcon: Int,
  setIcon: Int,
  tittleRise: String,
  tittleSet: String
) {
  Box(
    modifier = modifier
      .size(width = 166.dp, height = 136.dp)
  ) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .alpha(itemAlpha)
        .drawBehind {
          drawCircle(
            brush = Brush.verticalGradient(gradient),
            center = Offset(
              x = center.x,
              y = center.y
            ),
            radius = 66.dp.toPx()
          )
        }
    )

    Column(
      modifier = modifier
        .fillMaxSize()
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 44.dp),
        text = duration,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier
            .padding(start = 6.dp),
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.Top
        ) {
          Icon(
            modifier = Modifier
              .size(WEATHER_ICON_SIZE_36.dp),
            imageVector = ImageVector.vectorResource(id = riseIcon),
            tint = Color.Unspecified,
            contentDescription = stringResource(R.string.details_content_description_rise_icon)
          )
          Text(
            text = rise,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 10.sp
          )
          Text(
            text = tittleRise,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = 10.sp
          )
        }

        Column(
          modifier = Modifier
            .padding(end = 6.dp),
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.Top
        ) {
          Icon(
            modifier = Modifier
              .size(WEATHER_ICON_SIZE_36.dp),
            imageVector = ImageVector.vectorResource(id = setIcon),
            tint = Color.Unspecified,
            contentDescription = stringResource(R.string.details_content_description_set_icon)
          )
          Text(
            text = set,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 10.sp
          )
          Text(
            text = tittleSet,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = 10.sp
          )
        }
      }
    }
  }
}