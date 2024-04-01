package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import android.os.Parcelable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.WeatherScreen
import info.sergeikolinichenko.myapplication.presentation.ui.theme.Gradient
import info.sergeikolinichenko.myapplication.utils.formattedOnlyDay
import info.sergeikolinichenko.myapplication.utils.formattedOnlyHour
import info.sergeikolinichenko.myapplication.utils.toCalendar
import info.sergeikolinichenko.myapplication.utils.toHumidity
import info.sergeikolinichenko.myapplication.utils.toRoundToInt
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 09.03.2024 at 18:13 (GMT+3) **/
private const val MIN_VISIBLE_BARS_COUNT = 12
private val PAD_CANVAS_START = 40.dp
private val PAD_CANVAS_END = 4.dp
private val PAD_CANVAS_TOP = 4.dp
private val PAD_CANVAS_BOTTOM = 36.dp

// weather graphs on the details screen
@Composable
internal fun WeatherCharts(
  modifier: Modifier = Modifier,
  listWeather: List<WeatherScreen>,
  gradient: Gradient
) {
  var state by rememberAirPressureGraphState(listWeather = listWeather)
  val textMeasurer = rememberTextMeasurer()
  var transformableState: TransformableState? = null
  val iconUvIndex = painterResource(id = R.drawable.uv_index)
  val iconPressure = painterResource(id = R.drawable.atm_pressure)
  val iconHumidity = painterResource(id = R.drawable.humidity)

  Box(
    modifier = modifier
      .background(brush = gradient.secondaryGradient)
  ) {

    DrawGraphPressure(
      state = state,
      onSizeChanged = { state = it },
      onTransformableState = { transformableState = it }
    )

    DrawLineGraph(
      listItems = state.listWeather.map { it.uv.toInt() },
      minItem = state.minUv.toInt(),
      color = Color.Red,
      pxPerPoint = state.pxPerPointUv,
      scrolledBy = state.scrolledBy,
      barWidth = state.barWidth,
      transformableState = transformableState
    )

    DrawLineGraph(
      listItems = state.listWeather.map { it.humidity },
      minItem = state.minHumidity,
      color = Color.Blue,
      pxPerPoint = state.pxPerPointHum,
      scrolledBy = state.scrolledBy,
      barWidth = state.barWidth,
      transformableState = transformableState
    )

    DrawVerticalDelimiters(
      state = state,
      textMeasurer = textMeasurer,
      transformableState = transformableState
    )

    DrawDays(
      state = state,
      textMeasurer = textMeasurer,
      transformableState = transformableState
    )

    DrawHorizontalDelimiters(
      state = state,
      textMeasurer = textMeasurer
    )

    DrawSignatureGraphics(
      textMeasurer = textMeasurer,
      iconPressure = iconPressure,
      iconUv = iconUvIndex,
      iconHumidity = iconHumidity
    )
  }
}
@Composable
private fun DrawLineGraph(
  modifier: Modifier = Modifier,
  listItems: List<Int>,
  minItem: Int,
  color: Color,
  pxPerPoint: Float,
  scrolledBy: Float,
  barWidth: Float,
  transformableState: TransformableState?
) {
  Canvas(
    modifier = modifier
      .fillMaxSize()
      .padding(
        start = PAD_CANVAS_START,
        end = PAD_CANVAS_END,
        top = PAD_CANVAS_TOP,
        bottom = PAD_CANVAS_BOTTOM
      )
      .transformable(transformableState!!)
      .clipToBounds()
  ) {
    val firstItem = listItems.first()
    translate(left = scrolledBy) {
      drawPath(
        color = color,
        style = Stroke(
          width = 4.dp.toPx(),
          pathEffect = PathEffect.cornerPathEffect(20.dp.toPx())
        ),
        path = Path().apply {
          moveTo(
            x = 0f,
            y = size.height -
                (firstItem - minItem) * pxPerPoint
          )
          listItems.forEachIndexed { index, value ->
            if (value != firstItem) {
              val offsetX = barWidth * index - barWidth / 2
              lineTo(
                x = offsetX,
                y = size.height -
                    (value - minItem) * pxPerPoint
              )
            }
          }
        }
      )
    }
  }
}
@Composable
private fun DrawGraphPressure(
  modifier: Modifier = Modifier,
  state: GraphState,
  onSizeChanged: (GraphState) -> Unit,
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
    var previousBar = state.listWeather.first().airPressure

    translate(left = state.scrolledBy) {
      state.listWeather.forEachIndexed { index, value ->

        val offsetX = state.barWidth * index - state.barWidth / 2
        val color = if (value.airPressure > previousBar) Color.Cyan else Color.Green
        previousBar = value.airPressure

        drawLine(
          color = color,
          start = Offset(x = offsetX, size.height),
          end = Offset(offsetX, (value.airPressure - state.minPressure) * state.pxPerPointPres),
          strokeWidth = state.barWidth - 2.dp.toPx()
        )
      }
    }
  }
}

@Composable
private fun DrawDays(
  modifier: Modifier = Modifier,
  state: GraphState,
  textMeasurer: TextMeasurer,
  transformableState: TransformableState?
) {
  val colorOnBackground = MaterialTheme.colorScheme.onBackground

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .transformable(transformableState!!)
      .clipToBounds()
      .padding(
        start = PAD_CANVAS_START,
        end = PAD_CANVAS_END,
        top = PAD_CANVAS_TOP
      )
      .clipToBounds()
      .padding(bottom = 30.dp)

  ) {
    val listDays = state.listWeather.distinctBy {
      it.date.toCalendar().formattedOnlyDay()
    }

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
  rotate(
    degrees = -90f,
    pivot = Offset(x = offsetX - 4.dp.toPx(), y = size.height + heightText * 1.5f)
  ) {
    drawText(
      textLayoutResult = textLayoutResult,
      topLeft = Offset(
        x = offsetX + textLayoutResult.size.width / 2,
        y = size.height + heightText * 1.5f + 1.dp.toPx()
      )
    )
  }
}

@Composable
private fun DrawVerticalDelimiters(
  modifier: Modifier = Modifier,
  state: GraphState,
  textMeasurer: TextMeasurer,
  transformableState: TransformableState?
) {
  val colorOnBackground = MaterialTheme.colorScheme.onBackground

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .transformable(transformableState!!)
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

        if (index == 0 || ((index + 1) % 6) == 0) {
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
    topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height + 1.dp.toPx())
  )
}

@Composable
private fun DrawHorizontalDelimiters(
  modifier: Modifier = Modifier,
  state: GraphState,
  textMeasurer: TextMeasurer,
) {

  val colorOnBackground = MaterialTheme.colorScheme.onBackground

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
      state = state,
      color = colorOnBackground,
      textMeasurer = textMeasurer
    )
  }
}

private fun DrawScope.drawLevels(
  state: GraphState,
  color: Color = Color.White,
  textMeasurer: TextMeasurer
) {

  val drawCaptionOffsetX = 2.dp.toPx()
  //max
  val maxOffsetY = 0f
  drawDashedLine(
    start = Offset(0f, maxOffsetY),
    end = Offset(size.width, maxOffsetY),
    color = color
  )
  val sizeOfFont = 10.sp
  drawCaptionPress(
    textMeasurer = textMeasurer,
    caption = state.maxPressure.toRoundToInt(),
    offsetX = drawCaptionOffsetX,
    offsetY = maxOffsetY,
    fontSize = sizeOfFont,
    color = color
  )
  drawCaptionUv(
    textMeasurer = textMeasurer,
    caption = state.maxUv.toRoundToInt(),
    offsetX = drawCaptionOffsetX,
    offsetY = maxOffsetY,
    fontSize = sizeOfFont,
  )
  drawCaptionHum(
    textMeasurer = textMeasurer,
    caption = state.maxHumidity.toHumidity(),
    offsetX = drawCaptionOffsetX,
    offsetY = maxOffsetY,
    fontSize = sizeOfFont,
  )
  //min
  val minPressureOffsetY = size.height
  drawDashedLine(
    start = Offset(0f, minPressureOffsetY),
    end = Offset(size.width, minPressureOffsetY),
    color = color
  )
  drawCaptionPress(
    textMeasurer = textMeasurer,
    caption = state.minPressure.toRoundToInt(),
    offsetX = drawCaptionOffsetX,
    offsetY = minPressureOffsetY,
    color = color,
    isTop = true
  )
  drawCaptionUv(
    textMeasurer = textMeasurer,
    caption = state.minUv.toRoundToInt(),
    offsetX = drawCaptionOffsetX,
    offsetY = minPressureOffsetY,
    isTop = true
  )
  drawCaptionHum(
    textMeasurer = textMeasurer,
    caption = state.minHumidity.toHumidity(),
    offsetX = drawCaptionOffsetX,
    offsetY = minPressureOffsetY,
    isTop = true
  )
}

private fun DrawScope.drawCaptionPress(
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

private fun DrawScope.drawCaptionUv(
  textMeasurer: TextMeasurer,
  caption: String,
  offsetX: Float,
  offsetY: Float,
  color: Color = Color.Red,
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
  val cOffsetY = if (isTop) offsetY - (textLayoutResult.size.height * 3)
  else offsetY + textLayoutResult.size.height * 2
  drawText(
    textLayoutResult = textLayoutResult,
    topLeft = Offset(offsetX, cOffsetY)
  )
}

private fun DrawScope.drawCaptionHum(
  textMeasurer: TextMeasurer,
  caption: String,
  offsetX: Float,
  offsetY: Float,
  color: Color = Color.Blue,
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
  val cOffsetY = if (isTop) offsetY - textLayoutResult.size.height * 2
  else offsetY + textLayoutResult.size.height
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

@Composable
private fun DrawSignatureGraphics(
  modifier: Modifier = Modifier,
  textMeasurer: TextMeasurer,
  iconPressure: Painter,
  iconUv: Painter,
  iconHumidity: Painter
) {

  val color = MaterialTheme.colorScheme.onBackground
  val textPres = stringResource(id = R.string.weather_charts_title_pressure)
  val textTemp = stringResource(R.string.weather_charts_title_uv_index)
  val textHum = stringResource(R.string.weather_charts_title_humidity)

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .padding(
        start = 10.dp,
        end = PAD_CANVAS_END,
        top = PAD_CANVAS_TOP,
        bottom = 4.dp
      )
      .clipToBounds()
  ) {

    val textStyle = TextStyle(
      color = color,
      fontSize = 10.sp,
      fontWeight = FontWeight.W600
    )

    val textLayoutResultPres = textMeasurer.measure(
      text = textPres,
      style = textStyle
    )

    val textLayoutResultTemp = textMeasurer.measure(
      text = textTemp,
      style = textStyle
    )

    val textLayoutResultHum = textMeasurer.measure(
      text = textHum,
      style = textStyle
    )

    translate(
      left = 0f,
      top = size.height - 16.dp.toPx()
    ) {
      with(iconPressure) {
        draw(
          size = Size(14.dp.toPx(), 14.dp.toPx()),
          colorFilter = ColorFilter.tint(Color.Green)
        )
      }
    }

    drawText(
      textLayoutResult = textLayoutResultPres,
      color = Color.Cyan,
      topLeft = Offset(
        x = 18.dp.toPx(),
        y = size.height - 8.dp.toPx() - textLayoutResultPres.size.height / 2
      )
    )

    translate(
      left = 28.dp.toPx() + textLayoutResultPres.size.width,
      top = size.height - 16.dp.toPx()
    ) {
      with(iconUv) {
        draw(
          size = Size(14.dp.toPx(), 14.dp.toPx()),
          colorFilter = ColorFilter.tint(Color.Red)
        )
      }
    }

    drawText(
      textLayoutResult = textLayoutResultTemp,
      color = Color.Red,
      topLeft = Offset(
        x = 44.dp.toPx() + textLayoutResultPres.size.width,
        y = size.height - 8.dp.toPx() - textLayoutResultTemp.size.height / 2
      )
    )

    translate(
      left = 54.dp.toPx() + textLayoutResultPres.size.width + textLayoutResultTemp.size.width,
      top = size.height - 16.dp.toPx()
    ) {
      with(iconHumidity) {
        draw(
          size = Size(14.dp.toPx(), 14.dp.toPx()),
          colorFilter = ColorFilter.tint(Color.Blue)
        )
      }
    }

    drawText(
      textLayoutResult = textLayoutResultHum,
      color = Color.Blue,
      topLeft = Offset(
        x = 70.dp.toPx() + textLayoutResultPres.size.width + textLayoutResultTemp.size.width,
        y = size.height - 8.dp.toPx() - textLayoutResultHum.size.height / 2
      )
    )
  }
}

@Parcelize
private data class GraphState(
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

  // min and max pressure for visible bars
  val minPressure: Float
    get() = visibleBars.minOf { it.airPressure }
  val maxPressure: Float
    get() = visibleBars.maxOf { it.airPressure }
  val pxPerPointPres: Float
    get() = windowHeight / (maxPressure - minPressure)

  // min and max humidity for visible bars
  val minHumidity: Int
    get() = visibleBars.minOf { it.humidity }
  val maxHumidity: Int
    get() = visibleBars.maxOf { it.humidity }
  val pxPerPointHum: Float
    get() = windowHeight / (maxHumidity - minHumidity)

  // min and max uv for visible bars
  val minUv: Float
    get() = visibleBars.minOf { it.uv }
  val maxUv: Float
    get() = visibleBars.maxOf { it.uv }
  val pxPerPointUv: Float
    get() = windowHeight / (maxUv - minUv)
}

@Composable
private fun rememberAirPressureGraphState(
  listWeather: List<WeatherScreen>
): MutableState<GraphState> {
  return rememberSaveable { mutableStateOf(GraphState(listWeather)) }
}
