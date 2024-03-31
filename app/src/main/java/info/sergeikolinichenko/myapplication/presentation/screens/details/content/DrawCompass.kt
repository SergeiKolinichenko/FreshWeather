package info.sergeikolinichenko.myapplication.presentation.screens.details.content

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.utils.toDegree

/** Created by Sergei Kolinichenko on 19.03.2024 at 18:32 (GMT+3) **/
@Composable
internal fun DrawCompass(
  modifier: Modifier = Modifier,
  windDirection: String
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {

    val textMeasurer = rememberTextMeasurer()
    val color = MaterialTheme.colorScheme.onBackground

    DrawNameDirections(
      modifier = Modifier.size(30.dp)
    )

    DrawCaptionSidesOfWorld(
      modifier = Modifier.fillMaxSize(),
      textMeasurer = textMeasurer,
      color = color,
      fontSize = 14.sp
    )

    DrawCircleTriangles(
      modifier = Modifier.fillMaxSize()
    )
    DrawPointerDirections(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      degrees = windDirection.toDegree(),
    )
  }
}

@Composable
private fun DrawPointerDirections(
  modifier: Modifier = Modifier,
  degrees: Float = 0f
) {

  val color = if (isSystemInDarkTheme()) colorResource(id = R.color.purple_200)
  else colorResource(id = R.color.purple_600)

  Canvas(modifier = modifier) {
    rotate(degrees = degrees, center) {
      val triangleWidth = size.width / 15
      drawPath(
        color = color,
        path = Path().apply {
          moveTo(center.x, 0f)
          lineTo(center.x - triangleWidth.dp.toPx(), triangleWidth.dp.toPx())
          lineTo(center.x + triangleWidth.dp.toPx(), triangleWidth.dp.toPx())
          lineTo(center.x, 0f)
        }
      )
    }
  }
}

@Composable
private fun DrawCircleTriangles(
  modifier: Modifier = Modifier
) {

  val colorRed = if (isSystemInDarkTheme()) colorResource(id = R.color.red_200)
  else colorResource(id = R.color.red_600)

  val colorBlue = if (isSystemInDarkTheme()) colorResource(id = R.color.blue_200)
  else colorResource(id = R.color.blue_600)

  Canvas(modifier = modifier) {

    var degrees = 0f
    val triangleWidth = size.width / 40
    var offsetY: Float
    var triangleColor: Color

    for (item in 1..16) {

      if (item == 3 || item == 7 || item == 11 || item == 15) {
        offsetY = triangleWidth.dp.toPx() + triangleWidth.dp.toPx() / 2
        triangleColor = colorRed
      } else {
        offsetY = triangleWidth.dp.toPx()
        triangleColor = colorBlue
      }

      rotate(degrees = degrees, center) {

        // Skip the drawing of the triangle in the specified positions
        if (item != 1 && item != 5 && item != 9 && item != 13) {

          drawPath(
            color = triangleColor,
            path = Path().apply {
              moveTo(center.x, offsetY)
              lineTo(center.x - triangleWidth.dp.toPx(), 0f)
              lineTo(center.x + triangleWidth.dp.toPx(), 0f)
              lineTo(center.x, offsetY)
            }
          )
        }
      }
      degrees += 22.5f
    }
  }
}

@Composable
private fun DrawNameDirections(
  modifier: Modifier = Modifier,
) {
  Icon(
    modifier = modifier.background(Color.Transparent),
    painter = painterResource(id = R.drawable.wind),
    contentDescription = null
  )
}

@Composable
private fun DrawCaptionSidesOfWorld(
  modifier: Modifier = Modifier,
  textMeasurer: TextMeasurer,
  color: Color = Color.Black,
  fontSize: TextUnit = 10.sp,
  fontWeight: FontWeight = FontWeight.W600,
) {
  val textLayResN = textMeasurer.measure(
    text = "N",
    style = TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  )
  val textLayResS = textMeasurer.measure(
    text = "S",
    style = TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  )
  val textLayResE = textMeasurer.measure(
    text = "E",
    style = TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  )
  val textLayResW = textMeasurer.measure(
    text = "W",
    style = TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  )
  Canvas(modifier = modifier) {
    drawText(
      textLayoutResult = textLayResN,
      topLeft = Offset(
        center.x - textLayResN.size.width / 2,
        0f - textLayResN.size.height / 2
      )
    )
    drawText(
      textLayoutResult = textLayResS,
      topLeft = Offset(
        center.x - textLayResS.size.width / 2,
        size.height - textLayResS.size.height / 2
      )
    )
    drawText(
      textLayoutResult = textLayResE,
      topLeft = Offset(
        size.width - textLayResE.size.width / 2,
        center.y - textLayResE.size.height / 2
      )
    )
    drawText(
      textLayoutResult = textLayResW,
      topLeft = Offset(
        0f - textLayResE.size.width / 2,
        center.y - textLayResW.size.height / 2
      )
    )
  }
}