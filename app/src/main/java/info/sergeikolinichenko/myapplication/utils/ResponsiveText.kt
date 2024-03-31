package info.sergeikolinichenko.myapplication.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/** Created by Sergei Kolinichenko on 02.11.2023 at 16:59 (GMT+3) **/

@Composable
fun ResponsiveText(
  modifier: Modifier = Modifier,
  text: String,
  color: Color = MaterialTheme.colorScheme.onBackground,
  textAlign: TextAlign = TextAlign.Center,
  textStyle: TextStyle,
  targetTextSizeHeight: TextUnit = textStyle.fontSize,
  maxLines: Int = 1,
) {
  var textSize by remember { mutableStateOf(targetTextSizeHeight) }

  Text(
    modifier = modifier,
    text = text,
    color = color,
    textAlign = textAlign,
    fontSize = textSize,
    fontFamily = textStyle.fontFamily,
    fontStyle = textStyle.fontStyle,
    fontWeight = textStyle.fontWeight,
    lineHeight = textStyle.lineHeight,
    maxLines = maxLines,
    overflow = TextOverflow.Ellipsis,
    onTextLayout = { textLayoutResult ->
      val maxCurrentLineIndex: Int = textLayoutResult.lineCount - 1

      if (textLayoutResult.isLineEllipsized(maxCurrentLineIndex)) {
        textSize = textSize.times(TEXT_SCALE_REDUCTION_INTERVAL)
      }
    },
  )
}

private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.9f