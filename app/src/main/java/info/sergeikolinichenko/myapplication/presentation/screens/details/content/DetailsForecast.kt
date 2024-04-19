package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient
import info.sergeikolinichenko.myapplication.utils.ResponsiveText
import info.sergeikolinichenko.myapplication.utils.formattedFullDate
import info.sergeikolinichenko.myapplication.utils.fromKphToStringId
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toCelsiusString
import info.sergeikolinichenko.myapplication.utils.toPerCent
import info.sergeikolinichenko.myapplication.utils.toPrecipitation
import info.sergeikolinichenko.myapplication.utils.toRoundToInt
import info.sergeikolinichenko.myapplication.utils.toRoundToIntString

/** Created by Sergei Kolinichenko on 24.03.2024 at 14:52 (GMT+3) **/
private val SIZE_DETAILS_ICONS = 18.dp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailsForecast(
  modifier: Modifier = Modifier,
  forecast: Forecast,
  gradient: Gradient
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.SpaceEvenly,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    ResponsiveText(
      modifier = Modifier.padding(horizontal = 10.dp),
      text = forecast.forecastCurrent.date.toCalendar(forecast.forecastLocation.tzId)
        .formattedFullDate(),
      textStyle = MaterialTheme.typography.titleLarge
    )

    HorizontalDivider(
      modifier = Modifier.padding(
        start = 20.dp,
        end = 20.dp,
        top = 4.dp,
        bottom = 4.dp
      ),
      color = gradient.shadowColor
    )

    Text(
      text = forecast.forecastCurrent.descriptionText,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.W500
    )
    // Block with current temperature, maximum and minimum temperatures for that day,
    // current weather icon
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 6.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {

      Column(
        modifier = Modifier
          .fillMaxHeight()
          .padding(start = 4.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.Start
      ) {
        // Block with maximum and minimum temperatures for that day
        Row(
          modifier = Modifier
            .padding(start = 2.dp, top = 2.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.Bottom
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
          ) {
            Icon(
              modifier = Modifier.size(SIZE_DETAILS_ICONS),
              painter = painterResource(id = R.drawable.temperature_min),
              contentDescription = "icon temperature min"
            )
            Text(
              text = forecast.forecastCurrent.minTempC.toCelsiusString(),
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.W600
              )
            )
          }
          Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
          ) {
            Icon(
              modifier = Modifier.size(SIZE_DETAILS_ICONS),
              painter = painterResource(id = R.drawable.temperature_max),
              contentDescription = "icon temperature max"
            )
            Text(
              text = forecast.forecastCurrent.maxTempC.toCelsiusString(),
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.W600
              )
            )
          }
        }

        // Block with current temperature, filling temperatures for that day
        val fontSizeTemperature = 80
        Text(
          buildAnnotatedString {
            withStyle(
              SpanStyle(
                fontSize = fontSizeTemperature.sp,
                fontWeight = FontWeight.W600
              )
            ) {
              append(forecast.forecastCurrent.tempC.toRoundToIntString())
            }
            withStyle(
              SpanStyle(
                fontSize = fontSizeTemperature.sp / 2,
                fontWeight = FontWeight.W600
              )
            ) {
              append("°C")
            }
          },
        )
        // Block with "feels like" temperature
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            modifier = Modifier.size(SIZE_DETAILS_ICONS),
            painter = painterResource(id = R.drawable.thermometer),
            contentDescription = null
          )
          Spacer(modifier = Modifier.padding(4.dp))
          Text(
            text = stringResource(
              R.string.details_content_title_feels_like,
              forecast.forecastCurrent.feelsLikeC.toCelsiusString()
            ),
            style = MaterialTheme.typography.bodySmall,
          )
        }

      }
      // Icon with current weather icon
      GlideImage(
        modifier = Modifier
          .size(160.dp),
        model = forecast.forecastCurrent.condIconUrl,
        contentDescription = stringResource(R.string.details_content_text_description_weather_condition)
      )
    }

    HorizontalDivider(
      modifier = Modifier.padding(
        start = 20.dp,
        end = 20.dp,
        top = 4.dp,
        bottom = 4.dp
      ),
      color = gradient.shadowColor
    )

    // Block with additional information about current weather
    DetailsCurrentWeather(forecast = forecast)

    // Block with weather in the form of a chart
    WeatherCharts(
      modifier = Modifier
        .fillMaxWidth()
        .size(150.dp)
        .padding(vertical = 4.dp, horizontal = 8.dp),
      forecast = forecast,
      gradient = gradient
    )

    // Block with upcoming weather for the next hours
    AnimatedUpcomingHourlyWeather(
      forecast = forecast,
      gradient = gradient
    )
    // Block with upcoming weather for the next days
    AnimatedUpcomingDailyWeather(
      forecast = forecast,
      gradient = gradient
    )
    Spacer(modifier = Modifier.weight(0.5f))
  }
}

@Composable
private fun DetailsCurrentWeather(
  modifier: Modifier = Modifier,
  forecast: Forecast
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(4.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {

    Column(
      modifier = modifier
        .padding(start = 4.dp, end = 2.dp),
      horizontalAlignment = Alignment.Start
    ) {

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.rain),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_precipitation_mm,
            forecast.forecastCurrent.precipMm.toPrecipitation()
          ),
          style = MaterialTheme.typography.bodySmall
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.cloudy_day),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_cloudy,
            forecast.forecastCurrent.cloud.toPerCent()
          ),
          style = MaterialTheme.typography.bodySmall,
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.humidity),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_humidity,
            forecast.forecastCurrent.humidity
          ),
          style = MaterialTheme.typography.bodySmall,
        )
      }
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.atm_pressure),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_atmospheric_pressure_mbar,
            forecast.forecastCurrent.pressureMb.toRoundToInt()
          ),
          style = MaterialTheme.typography.bodySmall,
        )
      }
      // Block with UV index
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.uv_index),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_uv_index,
            forecast.forecastCurrent.uv.toRoundToInt()
          ),
          style = MaterialTheme.typography.bodySmall
        )
      }
      // Block with wind speed
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(SIZE_DETAILS_ICONS),
          painter = painterResource(id = R.drawable.wind),
          contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          text = stringResource(
            R.string.details_content_wind_speed
          ) +
              stringResource(id = forecast.forecastCurrent.windKph.fromKphToStringId()),
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }
    // Wind direction compass
    DrawCompass(
      modifier = Modifier
        .size(100.dp)
        .padding(horizontal = 6.dp),
      windDirection = forecast.forecastCurrent.windDir
    )
  }
}
