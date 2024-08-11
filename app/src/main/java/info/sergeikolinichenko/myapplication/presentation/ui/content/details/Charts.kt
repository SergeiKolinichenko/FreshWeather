package info.sergeikolinichenko.myapplication.presentation.ui.content.details

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.domain.entity.Forecast
import info.sergeikolinichenko.domain.entity.HourForecast
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.ChartsHourlyScreenValues
import info.sergeikolinichenko.myapplication.utils.LinearGradient.gradientHumidityChart
import info.sergeikolinichenko.myapplication.utils.LinearGradient.gradientPressureChart
import info.sergeikolinichenko.myapplication.utils.LinearGradient.gradientUvIndexChart
import info.sergeikolinichenko.myapplication.utils.TITLE_ICON_SIZE
import info.sergeikolinichenko.myapplication.utils.convertLongToCalendarWithTz
import info.sergeikolinichenko.myapplication.utils.formattedFullHour
import info.sergeikolinichenko.myapplication.utils.toIconId
import info.sergeikolinichenko.myapplication.utils.toListChartsHourlyScreenValues
import info.sergeikolinichenko.myapplication.utils.toPerCentFromInt
import info.sergeikolinichenko.myapplication.utils.toRoundToIntToString
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 25.07.2024 at 18:48 (GMT+3) **/

private const val MAXIMUM_HOURS = 25L
private const val MIN_VISIBILITY_COUNT = 6

@Composable
fun Charts(
  modifier: Modifier = Modifier,
  forecast: Forecast
) {

  val list = forecast.getSublistForecastHourly()

  val chartsState = rememberChartState(list = list.toListChartsHourlyScreenValues())

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(280.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    ) {
      if (list.isEmpty()) {
        Text(
          text = stringResource(R.string.details_content_error_date_set_on_phone),
          style = MaterialTheme.typography.titleLarge,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground
        )
      } else {

        Chart(
          list = list,
          tz = forecast.tzId,
          state = chartsState,
          changeState = { chartsState.value = it }
        )
        // title
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {

          val textId = when (chartsState.value.displayedItem) {
            Displayed.UV_INDEX -> R.string.details_content_carts_title_uv_index_chart
            Displayed.HUMIDITY -> R.string.details_content_carts_title_humidity_chart
            Displayed.PRESSURE -> R.string.details_content_carts_title_pressure_chart
          }

          Icon(
            modifier = Modifier
              .size(TITLE_ICON_SIZE.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.chart),
            contentDescription = stringResource(R.string.details_content_description_graph_icon)
          )
          Text(
            text = stringResource(textId),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground
          )
          // buttons
          DisplayedValues(
            onSelect = chartsState.value.displayedItem
          ) {
            chartsState.value = chartsState.value.copy(displayedItem = it)
          }
        }
      }
    }
  }
}

@Composable
private fun Chart(
  modifier: Modifier = Modifier,
  list: List<HourForecast>,
  state: State<ChartsState>,
  tz: String,
  changeState: (ChartsState) -> Unit
) {

  val chartState = state.value

  val textMeasurer = rememberTextMeasurer()

  val textNow = stringResource(id = R.string.details_content_title_now_date)
  val textColor = MaterialTheme.colorScheme.onBackground
  val textStyle = MaterialTheme.typography.labelMedium

  val listOfIcon = if (chartState.displayedItem == Displayed.UV_INDEX) {
    list.map { resizeBitmapImage(it.icon.toIconId(), LocalContext.current) }
  } else null

  val icon = when (chartState.displayedItem) {
    Displayed.HUMIDITY -> resizeBitmapImage(R.mipmap.ic_chart_humidity, LocalContext.current)
    Displayed.PRESSURE -> resizeBitmapImage(R.mipmap.ic_chart_pressure, LocalContext.current)
    else -> null
  }

  val chartGradient = when (chartState.displayedItem) {
    Displayed.HUMIDITY -> gradientHumidityChart
    Displayed.PRESSURE -> gradientPressureChart
    else -> gradientUvIndexChart
  }

  val transformableState = rememberTransformableState { _, panChange, _ ->

    val scrolledBy = (chartState.scrolledBy + panChange.x)
      .coerceAtLeast(chartState.screenWidth - ((list.size - 2) * chartState.itemWidth))
      .coerceAtMost(0f)

    changeState(chartState.copy(scrolledBy = scrolledBy))
  }

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .clipToBounds()
      .transformable(state = transformableState)
      .onSizeChanged {
        changeState(
          chartState.copy(
            screenHeight = it.height.toFloat(), screenWidth = it.width.toFloat()
          )
        )
      }
  ) {

    if (chartState.screenWidth != 0f && chartState.screenHeight != 0f) {

      translate(left = chartState.scrolledBy) {

        // draw gradient filled area
        drawFilledArea(
          chartsState = chartState,
          gradient = chartGradient
        )

        // draw circles - hours
        drawCirclesWhereHours(
          chartsState = chartState
        )

        // draw text - hours
        drawTextHours(
          chartsState = chartState,
          textNow = textNow,
          tzId = tz,
          textMeasurer = textMeasurer,
          color = textColor
        )

        // draw text - uv index
        drawTextValue(
          chartsState = chartState,
          textMeasurer = textMeasurer,
          textStyle = textStyle
        )

        // draw text - pressure
        drawTextTitleValue(
          chartsState = chartState,
          textMeasurer = textMeasurer,
          textStyle = textStyle
        )

        // draw images
        drawImage(
          chartsState = chartState,
          images = listOfIcon,
          image = icon
        )
      }
    }


  }
}

private fun DrawScope.drawFilledArea(
  chartsState: ChartsState,
  gradient: List<Color>
) {

  val max = chartsState.max
  val min = chartsState.min
  val pxPerPoint = chartsState.pxPerPoint
  val footer = chartsState.footer

  Log.d(
    "TAG",
    "max: $max min: $min max - min: ${max - min} pxPerPoint: $pxPerPoint footer: $footer"
  )

  drawPath(
    path = Path()
      .apply {
        chartsState.list.forEachIndexed { index, item ->

          when (index) {
            0 -> {
              moveTo(
                0f,
                size.height - (pxPerPoint * getDisplayedValue(
                  chartsState.list[1],
                  chartsState
                )) - footer
              )
            }

            1 -> {
              lineTo(
                chartsState.itemWidth * index - (chartsState.itemWidth / 2),
                size.height - (pxPerPoint * getDisplayedValue(item, chartsState)) - footer
              )
            }

            chartsState.list.size - 1 -> {
              lineTo(
                chartsState.itemWidth * index - (chartsState.itemWidth),
                size.height - (pxPerPoint * getDisplayedValue(item, chartsState)) - footer
              )
            }

            else -> {
              lineTo(
                chartsState.itemWidth * index - (chartsState.itemWidth / 2),
                size.height - (pxPerPoint * getDisplayedValue(item, chartsState)) - footer
              )
            }
          }
        }

        lineTo(
          chartsState.itemWidth * (chartsState.list.size - 2),
          size.height
        )
        lineTo(0f, size.height)
        close()
      },
    brush = Brush.verticalGradient(
      colors = gradient,
      startY = size.height - (pxPerPoint * if (max - min == 0f) 1f else max - min),
      endY = size.height
    ),
    alpha = 0.40f
  )
}

private fun DrawScope.drawCirclesWhereHours(
  chartsState: ChartsState,
) {

  chartsState.list.forEachIndexed { index, item ->

    val value = getDisplayedValue(item, chartsState)

    if (index > 0 && index < chartsState.list.size - 1) {

      val offsetX = if (index == 1) (chartsState.itemWidth / 2)
      else chartsState.itemWidth * index - (chartsState.itemWidth / 2)

      drawCircle(
        color = Color.White,
        radius = 6.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(
          x = offsetX,
          y = size.height - (chartsState.pxPerPoint * value) - chartsState.footer
        )
      )
    }
  }
}

private fun DrawScope.drawTextHours(
  chartsState: ChartsState,
  textMeasurer: TextMeasurer,
  textNow: String,
  tzId: String,
  color: Color
) {

  chartsState.list.forEachIndexed { index, item ->

    val textLayoutResult = textMeasurer.measure(
      text = if (index == 1) textNow else
        convertLongToCalendarWithTz(
          item.date,
          tzId
        ).formattedFullHour(),
      style = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        color = color,
        fontSize = 12.sp
      )
    )

    if (index > 0 && index < chartsState.list.size - 1) {

      val offsetX = if (index == 1) (chartsState.itemWidth / 2) - textLayoutResult.size.width / 2
      else chartsState.itemWidth * index - (chartsState.itemWidth / 2) - textLayoutResult.size.width / 2

      drawText(
        textLayoutResult = textLayoutResult,
        topLeft = androidx.compose.ui.geometry.Offset(
          x = offsetX,
          y = size.height - textLayoutResult.size.height - 16.dp.toPx()
        )
      )
    }
  }
}

private fun DrawScope.drawTextValue(
  chartsState: ChartsState,
  textMeasurer: TextMeasurer,
  textStyle: TextStyle
) {

  val pxPerPoint = chartsState.pxPerPoint
  val footer = chartsState.footer


  chartsState.list.forEachIndexed { index, item ->

    val dispValue = getDisplayedValue(item, chartsState)

    val mainText = when (chartsState.displayedItem) {
      Displayed.UV_INDEX -> item.uv.toRoundToIntToString()
      Displayed.HUMIDITY -> item.humidity.toPerCentFromInt()
      Displayed.PRESSURE -> item.pressureString.substringBeforeLast('\n')
    }

    val textLayoutResult = textMeasurer.measure(
      text = mainText,
      style = textStyle.copy(textAlign = TextAlign.Center)
    )

    if (index > 0 && index < chartsState.list.size - 1) {

      val offsetX = if (index == 1) (chartsState.itemWidth / 2) - textLayoutResult.size.width / 2
      else chartsState.itemWidth * index - (chartsState.itemWidth / 2) - textLayoutResult.size.width / 2

      drawText(
        textLayoutResult = textLayoutResult,
        topLeft = androidx.compose.ui.geometry.Offset(
          x = offsetX,
          y = size.height - (pxPerPoint * dispValue) + 16.dp.toPx() - footer
        )
      )
    }
  }
}

private fun DrawScope.drawTextTitleValue(
  chartsState: ChartsState,
  textMeasurer: TextMeasurer,
  textStyle: TextStyle
) {

  if (chartsState.displayedItem == Displayed.PRESSURE) {

    val pxPerPoint = chartsState.pxPerPoint
    val footer = chartsState.footer


    chartsState.list.forEachIndexed { index, item ->

      val dispValue = getDisplayedValue(item, chartsState)

      val text = item.pressureString.substringAfterLast('\n')

      val textLayoutResult = textMeasurer.measure(
        text = text,
        style = textStyle.copy(fontSize = 10.sp, textAlign = TextAlign.Center)
      )

      if (index > 0 && index < chartsState.list.size - 1) {
        val offsetX = if (index == 1) (chartsState.itemWidth / 2) - textLayoutResult.size.width / 2
        else chartsState.itemWidth * index - (chartsState.itemWidth / 2) - textLayoutResult.size.width / 2

        drawText(
          textLayoutResult = textLayoutResult,
          topLeft = androidx.compose.ui.geometry.Offset(
            x = offsetX,
            y = size.height - (pxPerPoint * dispValue) + 14.dp.toPx() + textLayoutResult.size.height - footer
          )
        )
      }
    }
  }


}

private fun DrawScope.drawImage(
  chartsState: ChartsState,
  images: List<ImageBitmap>? = null,
  image: ImageBitmap? = null
) {

  val pxPerPoint = chartsState.pxPerPoint
  val footer = chartsState.footer

  chartsState.list.forEachIndexed { index, item ->

    val value = getDisplayedValue(item, chartsState)

    if (index > 0 && index < chartsState.list.size - 1 && (images != null || image != null)) {

      val img = if (images.isNullOrEmpty()) image else images[index]
      if (img == null) return@forEachIndexed

      val offsetX = if (index == 1) (chartsState.itemWidth / 2) - img.width / 2
      else chartsState.itemWidth * index - (chartsState.itemWidth / 2) - img.width / 2

      drawImage(
        image = img,
        topLeft = androidx.compose.ui.geometry.Offset(
          x = offsetX,
          y = size.height - (pxPerPoint * value) - 40.dp.toPx() - footer,
        ),
      )
    }
  }
}

@Composable
private fun DisplayedValues(
  modifier: Modifier = Modifier,
  onSelect: Displayed,
  onSelected: (Displayed) -> Unit
) {
  Row(
    modifier = modifier
      .wrapContentSize()
      .padding(bottom = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {

    Displayed.entries.forEach { displayed ->

      val iconId = when (displayed) {
        Displayed.UV_INDEX -> R.drawable.sun_max
        Displayed.HUMIDITY -> R.drawable.humidity
        Displayed.PRESSURE -> R.drawable.thermostat
      }

      val isDisplayed = displayed == onSelect

      AssistChip(
        modifier = Modifier
          .wrapContentSize(),
        onClick = { onSelected(displayed) },

        label = {
          Icon(
            modifier = Modifier.size(16.dp),
            imageVector = ImageVector.vectorResource(id = iconId),
            tint = if (isDisplayed) MaterialTheme.colorScheme.surface else Color.Unspecified,
            contentDescription = stringResource(R.string.details_content_description_uv_index_button_icon)
          )
        },
        colors = AssistChipDefaults.assistChipColors(
          containerColor = if (isDisplayed) MaterialTheme.colorScheme.primary else Color.Transparent,
//          labelColor = if (isDisplayed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
        )
      )
    }
  }

}

private enum class Displayed {
  UV_INDEX,
  HUMIDITY,
  PRESSURE
}

@Parcelize
private data class ChartsState(
  val list: List<ChartsHourlyScreenValues>,
  val visibleItemCount: Int = MIN_VISIBILITY_COUNT,
  val displayedItem: Displayed = Displayed.UV_INDEX,
  val screenWidth: Float = 0f,
  val screenHeight: Float = 0f,
  val scrolledBy: Float = 0f
) : Parcelable {

  val itemWidth: Float
    get() = screenWidth / visibleItemCount

  val max: Float
    get() = when (displayedItem) {
      Displayed.UV_INDEX -> list.maxOf { it.uv }
      Displayed.HUMIDITY -> list.maxOf { it.humidity.toFloat() }
      Displayed.PRESSURE -> list.maxOf { it.pressureFloat }
    }

  val min: Float
    get() = when (displayedItem) {
      Displayed.UV_INDEX -> list.drop(1).minOf { it.uv }
      Displayed.HUMIDITY -> list.drop(1).minOf { it.humidity.toFloat() }
      Displayed.PRESSURE -> list.drop(1).minOf { it.pressureFloat }
    }

  val pxPerPoint: Float
    get() = screenHeight / 6f / if (max - min == 0f) 1f else max - min

  val footer: Float
    get() = screenHeight / 2.7f

}

@Composable
private fun rememberChartState(list: List<ChartsHourlyScreenValues>): MutableState<ChartsState> {
  return rememberSaveable { mutableStateOf(ChartsState(list = list)) }
}

private fun resizeBitmapImage(imageId: Int, context: Context): ImageBitmap {
  val source = BitmapFactory.decodeResource(
    context.resources,
    imageId
  ).copy(Bitmap.Config.ARGB_8888, true)
  val aspect = source.width / source.height.toFloat()
  val required = 75.dp.value.toInt()
  val result =
    Bitmap.createScaledBitmap(source, required, required / aspect.roundToInt(), false)
  return result.asImageBitmap()
}

private fun getDisplayedValue(item: ChartsHourlyScreenValues, chartsState: ChartsState) =
  when (chartsState.displayedItem) {
    Displayed.UV_INDEX -> item.uv - chartsState.min
    Displayed.HUMIDITY -> item.humidity - chartsState.min
    Displayed.PRESSURE -> item.pressureFloat - chartsState.min
  }

private fun Forecast.getSublistForecastHourly(): List<HourForecast> {
  val now = LocalDateTime.now(ZoneId.of(this.tzId))

  return this.upcomingHours.filter { item ->

    val itemHour = LocalDateTime.ofInstant(
      Instant.ofEpochSecond(item.date),
      ZoneId.of(this.tzId)
    )

    itemHour > now.minusHours(2) && itemHour < now.plusHours(MAXIMUM_HOURS)
  }
}