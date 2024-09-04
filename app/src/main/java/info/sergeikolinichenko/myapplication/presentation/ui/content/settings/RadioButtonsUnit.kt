package info.sergeikolinichenko.myapplication.presentation.ui.content.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.myapplication.utils.ResponsiveText

/** Created by Sergei Kolinichenko on 17.07.2024 at 14:16 (GMT+3) **/

@Composable
internal fun RadioButtonsUnit(
  modifier: Modifier = Modifier,
  radioButtonClicked: RadioButtonClicked = RadioButtonClicked.TOP_BUTTON_CLICKED,
  unitTitle: Int,
  topButtonTitle: Int,
  bottomButtonTitle: Int,
  onClickTopButton: () -> Unit,
  onClickBottomButton: () -> Unit
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
  ) {
    ResponsiveText(
      modifier = Modifier
        .padding(16.dp),
      text = stringResource(unitTitle),
      targetTextSizeHeight = 16.sp,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground,
      maxLines = 1
    )

    // Top Button
    RadioButtonUnit(
      radioButtonClicked = radioButtonClicked == RadioButtonClicked.TOP_BUTTON_CLICKED,
      shapesRadioButtonUnit = ShapesRadioButtonUnit(10.dp, 10.dp, 0.dp, 0.dp),
      buttonTitle = topButtonTitle) { onClickTopButton() }

    Spacer(modifier = Modifier.height(2.dp))

    // Bottom Button
    RadioButtonUnit(
      radioButtonClicked = radioButtonClicked == RadioButtonClicked.BOTTOM_BUTTON_CLICKED,
      shapesRadioButtonUnit = ShapesRadioButtonUnit(0.dp, 0.dp, 10.dp, 10.dp),
      buttonTitle = bottomButtonTitle) { onClickBottomButton() }

  }
}
@Composable
private fun RadioButtonUnit(
  modifier: Modifier = Modifier,
  radioButtonClicked: Boolean,
  buttonTitle: Int,
  shapesRadioButtonUnit: ShapesRadioButtonUnit,
  onClickButton: () -> Unit,
){
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(62.dp)
      .clip(
        shape = RoundedCornerShape(
          topStart = shapesRadioButtonUnit.topStart,
          topEnd = shapesRadioButtonUnit.topEnd,
          bottomStart = shapesRadioButtonUnit.bottomStart,
          bottomEnd = shapesRadioButtonUnit.bottomEnd
        )
      )
      .background(MaterialTheme.colorScheme.surface),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {

    ResponsiveText(
      modifier = Modifier
        .padding(start = 16.dp),
      text = stringResource(buttonTitle),
      targetTextSizeHeight = 16.sp,
      lineHeight = 18.sp,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground,
    )

    RadioButton(
      modifier = Modifier.testTag(stringResource(buttonTitle)),
      selected = radioButtonClicked,
      onClick = { onClickButton() }
    )
  }
}

enum class RadioButtonClicked {
  TOP_BUTTON_CLICKED,
  BOTTOM_BUTTON_CLICKED
}
data class ShapesRadioButtonUnit(
  val topStart: Dp,
  val topEnd: Dp,
  val bottomStart: Dp,
  val bottomEnd: Dp
)