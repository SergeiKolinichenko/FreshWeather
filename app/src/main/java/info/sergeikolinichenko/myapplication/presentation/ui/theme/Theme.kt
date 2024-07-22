package info.sergeikolinichenko.myapplication.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = Purple80,
  secondary = PurpleGrey80,
  tertiary = Pink80,

  background = Color(0xFF050C1A),
  surface = Color(0xFFC9D8F7),
  surfaceContainer = Color(0xFFE6EDFC),
  onPrimary = Color(0xFFE6EDFC),
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFF091933),
  onSurface = Color(0xFF091933)
)

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF65558F),
  secondary = Color(0xFF625B71),
  tertiary = Color(0xFFAAC2E4),

  //Other default colors to override
  background = Color(0xFFC9D8F7),
  surface = Color(0xFFC9D8F7),
  surfaceVariant = Color(0xFFE6EDFC),
  surfaceContainer = Color(0xFFC9D8F7),
  onPrimary = Color(0xFFE6EDFC),
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFF091933),
  onSurface = Color(0xFF091933),
  )

@Composable
fun FreshWeatherTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}