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
  primary = Color(0xFFBBC6DB),
  secondary = Color(0xFF888C92),
  tertiary = Color(0xFF2A303C),

  background = Color(0xFF050C1A),
  surface = Color(0xFF141B28),
  surfaceVariant = Color(0xFF101520),
  surfaceContainer = Color(0xFFE6EDFC),
  surfaceBright = Color(0xFF141B28),
  onPrimary = Color(0xFFE6EDFC),
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFFF4FBF8),
  onSurface = Color(0xFFF4FBF8),
  outline = Color(0xFF050C1A),
  onSurfaceVariant = Color(0xFF7DC3F5),
  surfaceContainerLowest = Color(0xFF3E619C),
  surfaceContainerLow = Color(0xFF5D92D6),
  surfaceContainerHigh = Color(0xFF7DC3F5),
  surfaceContainerHighest = Color(0xFF9BD4FB)
)

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF335487),
  secondary = Color(0xFF8F9AAE),
  tertiary = Color(0xFFAAC2E4),

  //Other default colors to override
  background = Color(0xFFC9D8F7),
  surface = Color(0xFFE6EDFC),
  surfaceVariant = Color(0xFFC9D8F7),
  surfaceContainer = Color(0xFFC9D8F7),
  surfaceBright = Color(0xFFD8E4FA),
  onPrimary = Color(0xFFE6EDFC),
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFF091933),
  onSurface = Color(0xFF091933),
  outline = Color(0xFFD9E4F9),
  onSurfaceVariant = Color(0xFF5FAEE8),
  surfaceContainerLowest = Color(0xFF9BD4FB),
  surfaceContainerLow = Color(0xFF7DC3F5),
  surfaceContainerHigh = Color(0xFF5D92D6),
  surfaceContainerHighest = Color(0xFF3E619C)
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
      window.statusBarColor = colorScheme.background.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}