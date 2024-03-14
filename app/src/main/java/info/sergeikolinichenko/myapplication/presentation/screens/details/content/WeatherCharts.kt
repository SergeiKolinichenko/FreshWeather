package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.entity.WeatherScreen
import info.sergeikolinichenko.myapplication.utils.formattedOnlyDay
import info.sergeikolinichenko.myapplication.utils.formattedOnlyHour
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toPressure
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 09.03.2024 at 18:13 (GMT+3) **/
private const val MIN_VISIBLE_BARS_COUNT = 12
private val PAD_CANVAS_START = 40.dp
private val PAD_CANVAS_END = 4.dp
private val PAD_CANVAS_TOP = 4.dp
private val PAD_CANVAS_BOTTOM = 20.dp

@Composable
fun WeatherCharts(
  modifier: Modifier = Modifier,
  listWeather: List<WeatherScreen>
) {
  Log.d("MyLog", " WeatherCharts")
  var state by rememberAirPressureGraphState(listWeather = listWeather)
  val textMeasurer = rememberTextMeasurer()
  var transformableState: TransformableState? = null

  Box(
    modifier = modifier
  ) {

    DrawPressureGraph(
      state = state,
      onSizeChanged = { state = it },
      onTransformableState = { transformableState = it }
    )

    DrawDelimiters(
      state = state,
      textMeasurer = textMeasurer,
      transformableState = transformableState
    )

    DrawDays(
      state = state,
      textMeasurer = textMeasurer,
      transformableState = transformableState
    )

    DrawPressureInfo(
      state = state,
      textMeasurer = textMeasurer
    )

  }
}
@Composable
private fun DrawPressureGraph(
  modifier: Modifier = Modifier,
  state: AirPressureGraphState,
  onSizeChanged: (AirPressureGraphState) -> Unit,
  onTransformableState: (TransformableState) -> Unit
) {

  val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
    val visibleBarsCount = (state.visibleBarsCount / zoomChange).roundToInt()
      .coerceIn(MIN_VISIBLE_BARS_COUNT, state.listWeather.size)
    val scrolledBy = (state.scrolledBy + panChange.x)
      .coerceAtLeast(-(state.listWeather.size * state.barWidth - state.windowWidth))
      .coerceAtMost(0f)

    onSizeChanged(
      state.copy(
        visibleBarsCount = visibleBarsCount,
        scrolledBy = scrolledBy
      )
    )
  }
  onTransformableState(transformableState)
  Canvas(
    modifier = modifier
      .fillMaxSize()
      .padding(
        start = PAD_CANVAS_START,
        end = PAD_CANVAS_END,
        top = PAD_CANVAS_TOP,
        bottom = PAD_CANVAS_BOTTOM
      )
      .transformable(transformableState)
      .onSizeChanged {
        onSizeChanged(
          state.copy(
            windowWidth = it.width.toFloat(),
            windowHeight = it.height.toFloat()
          )
        )
      }
      .clipToBounds(),
  ) {
    val minPressure = state.minPressure
    val pxPerPoint = state.pxPerPoint
    var previousBar = state.listWeather.first().airPressure
    translate(left = state.scrolledBy) {
      state.listWeather.forEachIndexed { index, value ->
        val offsetX = state.barWidth * index - state.barWidth / 2
        val color = if (value.airPressure > previousBar) Color.Cyan else Color.Green
        previousBar = value.airPressure

        drawLine(
          color = color,
          start = Offset(x = offsetX, size.height),
          end = Offset(offsetX, (value.airPressure - minPressure) * pxPerPoint),
          strokeWidth = state.barWidth - 2.dp.toPx()
        )
      }
    }
  }
}
@Composable
private fun DrawDays(
  modifier: Modifier = Modifier,
  state: AirPressureGraphState,
  textMeasurer: TextMeasurer,
  transformableState: TransformableState?
) {
  val colorOnBackground = MaterialTheme.colorScheme.onBackground

  Canvas(modifier = modifier
    .fillMaxSize()
    .transformable(transformableState!!)
    .clipToBounds()
    .padding(
      start = PAD_CANVAS_START,
      end = PAD_CANVAS_END,
      top = PAD_CANVAS_TOP
    )
    .clipToBounds()
    .padding(bottom = 20.dp)

  ) {
    val listDays = state.listWeather.distinctBy { it.date.toCalendar().formattedOnlyDay() }

    translate(left = state.scrolledBy) {
      state.listWeather.forEachIndexed { index, weather ->
        listDays.forEach {
          if (it == weather) {
            drawDay(
              item = weather,
              color = colorOnBackground,
              offsetX = index * state.barWidth,
              textMeasurer = textMeasurer
            )
          }
        }
      }
    }
  }
}
private fun DrawScope.drawDay(
  item: WeatherScreen,
  color: Color,
  offsetX: Float,
  textMeasurer: TextMeasurer,
  fontSize: TextUnit = 10.sp,
  fontWeight: FontWeight = FontWeight.W600,
  lineLength: Dp = 4.dp,
  gapeLength: Dp = 4.dp
) {
  drawLine(
    color = color,
    start = Offset(x = offsetX, y = 0f),
    end = Offset(x = offsetX, y = size.height),
    strokeWidth = 1.dp.toPx(),
    pathEffect = PathEffect.dashPathEffect(
      intervals = floatArrayOf(lineLength.toPx(), gapeLength.toPx())
    )
  )
  val caption = item.date.toCalendar().formattedOnlyDay()
  val textLayoutResult = textMeasurer.measure(
    text = caption,
    style = TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  )
  val heightText = textLayoutResult.size.height
  drawText(
    textLayoutResult = textLayoutResult,
    topLeft = Offset(x = offsetX + 4.dp.toPx(), y = size.height - heightText * 1.5f)
  )
}
@Composable
private fun DrawDelimiters(
  modifier: Modifier = Modifier,
  state: AirPressureGraphState,
  textMeasurer: TextMeasurer,
  transformableState: TransformableState?
) {
  val colorOnBackground = MaterialTheme.colorScheme.onBackground

  Canvas(modifier = modifier
    .fillMaxSize()
    .transformable(transformableState!!)
    .clipToBounds()
    .padding(
      start = PAD_CANVAS_START,
      end = PAD_CANVAS_END,
      top = PAD_CANVAS_TOP
    )
    .clipToBounds()
    .padding(bottom = PAD_CANVAS_BOTTOM)

  ) {
    translate(left = state.scrolledBy) {
      state.listWeather.forEachIndexed { index, weather ->

        val offsetX = state.barWidth * index + state.barWidth / 2

        if (index == 0 || ((index + 1) % 6 ) == 0) {
          drawTimeDelimiter(
            item = weather,
            color = colorOnBackground,
            offsetX = offsetX,
            textMeasurer = textMeasurer
          )
        }
      }
    }

  }
}
private fun DrawScope.drawTimeDelimiter(
  item: WeatherScreen,
  color: Color,
  offsetX: Float,
  textMeasurer: TextMeasurer,
  fontSize: TextUnit = 10.sp,
  fontWeight: FontWeight = FontWeight.W600
) {
  drawLine(
    color = color,
    start = Offset(x = offsetX, y = size.height - 5.dp.toPx()),
    end = Offset(x = offsetX, y = size.height + 5.dp.toPx()),
    strokeWidth = 1.dp.toPx()
  )
  val caption = item.date.toCalendar().formattedOnlyHour()
  val textLayoutResult = textMeasurer.measure(
    text = caption,
    style = TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  )
  drawText(
    textLayoutResult = textLayoutResult,
    topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height)
  )
}
@Composable
private fun DrawPressureInfo(
  modifier: Modifier = Modifier,
  state: AirPressureGraphState,
  textMeasurer: TextMeasurer,
) {

  val colorOnBackground = MaterialTheme.colorScheme.onBackground
  val averagePressure = (state.maxPressure - state.minPressure) / 2 + state.minPressure

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .padding(
        bottom = PAD_CANVAS_BOTTOM,
        top = PAD_CANVAS_TOP,
        start = 4.dp,
        end = PAD_CANVAS_END
      )
  ) {
    drawLevels(
      maxPressure = state.maxPressure,
      minPressure = state.minPressure,
      averagePressure = averagePressure,
      pxPerPoint = state.pxPerPoint,
      color = colorOnBackground,
      textMeasurer = textMeasurer
    )
  }
}
private fun DrawScope.drawLevels(
  maxPressure: Float,
  minPressure: Float,
  averagePressure: Float,
  pxPerPoint: Float,
  color: Color = Color.White,
  textMeasurer: TextMeasurer
) {
  //max
  val maxPressureOffsetY = 0f
  drawDashedLine(
    start = Offset(0f, maxPressureOffsetY),
    end = Offset(size.width, maxPressureOffsetY),
    color = color
  )
  drawCaption(
    textMeasurer = textMeasurer,
    caption = maxPressure.toPressure(),
    offsetX = 4.dp.toPx(),
    offsetY = maxPressureOffsetY,
    color = color
  )
  //averagePressure
  val averagePressureOffsetY = (averagePressure - minPressure) * pxPerPoint
  val topBorder = size.height - size.height * 0.9
  val bottomBorder = size.height - size.height * 0.1
  if ( averagePressureOffsetY > topBorder && averagePressureOffsetY < bottomBorder) {
    drawDashedLine(
      start = Offset(0f, averagePressureOffsetY),
      end = Offset(size.width, averagePressureOffsetY),
      color = color
    )
    drawCaption(
      textMeasurer = textMeasurer,
      caption = averagePressure.toPressure(),
      offsetX = 4.dp.toPx(),
      offsetY = averagePressureOffsetY,
      color = color
    )
  }
  //min
  val minPressureOffsetY = size.height
  drawDashedLine(
    start = Offset(0f, minPressureOffsetY),
    end = Offset(size.width, minPressureOffsetY),
    color = color
  )
  drawCaption(
    textMeasurer = textMeasurer,
    caption = minPressure.toPressure(),
    offsetX = 4.dp.toPx(),
    offsetY = minPressureOffsetY,
    color = color,
    isTop = true
  )
}

private fun DrawScope.drawCaption(
  textMeasurer: TextMeasurer,
  caption: String,
  offsetX: Float,
  offsetY: Float,
  color: Color = Color.White,
  fontSize: TextUnit = 10.sp,
  fontWeight: FontWeight = FontWeight.W600,
  isTop: Boolean = false
) {
  val textLayoutResult = textMeasurer.measure(
    text = caption,
    style = TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  )
  val cOffsetY = if (isTop)
    offsetY - textLayoutResult.size.height
  else offsetY
  drawText(
    textLayoutResult = textLayoutResult,
    topLeft = Offset(offsetX, cOffsetY)
  )
}
private fun DrawScope.drawDashedLine(
  color: Color = Color.White,
  start: Offset,
  end: Offset,
  strokeWidth: Dp = 1.dp,
  lineLength: Dp = 4.dp,
  gapeLength: Dp = 4.dp
) {
  drawLine(
    color = color,
    start = start,
    end = end,
    strokeWidth = strokeWidth.toPx(),
    pathEffect = PathEffect.dashPathEffect(
      intervals = floatArrayOf(lineLength.toPx(), gapeLength.toPx())
    )
  )
}
@Parcelize
private data class AirPressureGraphState(
  val listWeather: List<WeatherScreen>,
  val visibleBarsCount: Int = 24,
  val windowWidth: Float = 0f,
  val windowHeight: Float = 0f,
  val scrolledBy: Float = 1f
) : Parcelable {
  val barWidth: Float
    get() = windowWidth / visibleBarsCount

  private val visibleBars: List<WeatherScreen>
    get() {
      val startIndex = if (scrolledBy == 0f) 0
      else (scrolledBy * -1 / barWidth).roundToInt().coerceAtLeast(0)

      val endIndex = (startIndex + visibleBarsCount).coerceAtMost(listWeather.size)
      return listWeather.subList(startIndex, endIndex)
    }
  val minPressure: Float
    get() = visibleBars.minOf { it.airPressure }
  val maxPressure: Float
    get() = visibleBars.maxOf { it.airPressure }
  val pxPerPoint: Float
    get() = windowHeight / (maxPressure - minPressure)

}

@Composable
private fun rememberAirPressureGraphState(
  listWeather: List<WeatherScreen>
): MutableState<AirPressureGraphState> {
  return rememberSaveable { mutableStateOf(AirPressureGraphState(listWeather)) }
}
