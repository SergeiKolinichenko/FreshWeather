package info.sergeikolinichenko.myapplication.utils

import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/** Created by Sergei Kolinichenko on 02.11.2023 at 16:59 (GMT+3) **/

@Composable
fun ResponsiveText(
  text: String,
  modifier: Modifier = Modifier,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  color: Color = Color.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  textAlign: TextAlign? = null,
  textStyle: TextStyle = LocalTextStyle.current,
  targetTextSizeHeight: TextUnit = textStyle.fontSize,
  maxLines: Int = Int.MAX_VALUE,
  softWrap: Boolean = true
) {
  var textSize by remember { mutableStateOf(targetTextSizeHeight) }

  Text(
    modifier = modifier,
    text = text,
    color = color,
    textAlign = textAlign,
    fontSize = textSize,
    fontFamily = fontFamily,
    fontStyle = fontStyle,
    fontWeight = fontWeight,
    lineHeight = lineHeight,
    maxLines = maxLines,
    overflow = TextOverflow.Ellipsis,
    style = textStyle,
    softWrap = softWrap,
    onTextLayout = { textLayoutResult ->
      val maxCurrentLineIndex: Int = textLayoutResult.lineCount - 1

      if (textLayoutResult.isLineEllipsized(maxCurrentLineIndex)) {
        textSize = textSize.times(TEXT_SCALE_REDUCTION_INTERVAL)
      }
    },
  )
}
private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.9f