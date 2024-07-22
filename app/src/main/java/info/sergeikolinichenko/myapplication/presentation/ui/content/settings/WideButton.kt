package info.sergeikolinichenko.myapplication.presentation.ui.content.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Created by Sergei Kolinichenko on 17.07.2024 at 14:15 (GMT+3) **/


@Composable
internal fun WideButton(
  modifier: Modifier = Modifier,
  textId: Int,
  onClick: () -> Unit
) {

  Box(
    modifier = modifier
      .height(56.dp)
      .fillMaxWidth()
      .clickable { onClick() },
  ) {
    Text(
      modifier = Modifier
        .padding(start = 16.dp)
        .align(Alignment.CenterStart)
        .fillMaxWidth(),
      text = stringResource(textId),
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      fontSize = 16.sp,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}