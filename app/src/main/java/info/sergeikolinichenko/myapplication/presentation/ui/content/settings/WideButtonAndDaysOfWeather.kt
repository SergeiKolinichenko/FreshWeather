package info.sergeikolinichenko.myapplication.presentation.ui.content.settings

import android.content.Context
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import info.sergeikolinichenko.myapplication.presentation.screens.settings.store.SettingsStore

/** Created by Sergei Kolinichenko on 17.07.2024 at 14:15 (GMT+3) **/

@Composable
internal fun WideButton(
  modifier: Modifier = Modifier,
  textId: Int,
  onClick: (context: Context) -> Unit
) {

  val context = LocalContext.current

  Box(
    modifier = modifier
      .height(56.dp)
      .fillMaxWidth()
      .clickable { onClick(context) },
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

@Composable
internal fun DaysOfWeather(
  modifier: Modifier = Modifier,
  state: State<SettingsStore.State>,
  daysOfWeather: (Int) -> Unit
) {

  val isKeyboardOpen by keyboardAsState()
  val focusManager = LocalFocusManager.current
  if (!isKeyboardOpen) focusManager.clearFocus()

  Column(
    modifier = modifier
      .fillMaxWidth()
  ) {
    Text(
      modifier = Modifier
        .wrapContentWidth()
        .padding(16.dp),
      text = "Forecast",
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      fontSize = 16.sp,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(2.dp))
    Row(
      modifier = modifier
        .fillMaxWidth()
        .height(62.dp)
        .clip(
          shape = RoundedCornerShape(10.dp)
        )
        .background(MaterialTheme.colorScheme.surface),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {

      Text(
        modifier = Modifier
          .width(220.dp)
          .padding(start = 16.dp),
        text = "Number of forecast days",
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onBackground
      )
      TextField(
        modifier = Modifier
          .width(140.dp),
        value = state.value.daysOfWeather.toString(),
        onValueChange = { newText ->
          daysOfWeather(newText.toIntOrNull() ?: 0)
        },
        textStyle = TextStyle(
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Medium,
          fontSize = 16.sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground
        ),
        leadingIcon = {
          Icon(
            modifier = Modifier.clickable {
              daysOfWeather(state.value.daysOfWeather - 1)
            },
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
            contentDescription = "Arrow left"
          )
        },
        trailingIcon = {
          Icon(
            modifier = Modifier.clickable {
              daysOfWeather(state.value.daysOfWeather +1 )
            },
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = "Arrow right"
          )
        },
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            focusManager.clearFocus()
          }
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          disabledIndicatorColor = Color.Transparent,
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          disabledContainerColor = MaterialTheme.colorScheme.surface,
          errorContainerColor = Color.Red.copy(alpha = 0.1f)
        )
      )
    }
  }
}

@Composable
fun keyboardAsState(): State<Boolean> {
  val keyboardState = remember { mutableStateOf(false) }
  val view = LocalView.current
  val viewTreeObserver = view.viewTreeObserver
  DisposableEffect(viewTreeObserver) {
    val listener = ViewTreeObserver.OnGlobalLayoutListener {
      keyboardState.value = ViewCompat.getRootWindowInsets(view)
        ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
    }
    viewTreeObserver.addOnGlobalLayoutListener(listener)
    onDispose { viewTreeObserver.removeOnGlobalLayoutListener(listener) }
  }
  return keyboardState
}