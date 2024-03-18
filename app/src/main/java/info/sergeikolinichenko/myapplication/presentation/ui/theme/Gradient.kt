package info.sergeikolinichenko.myapplication.presentation.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Created by Sergei Kolinichenko on 26.02.2024 at 16:30 (GMT+3) **/
data class Gradient(
  val primaryGradient: Brush,
  val secondaryGradient: Brush,
  val shadowColor: Color
) {
  constructor(
    firstColor: Color,
    secondColor: Color,
    thirdColor: Color,
    fourthColor: Color
  ) : this(
    primaryGradient = Brush.linearGradient(
      colors = listOf(firstColor, secondColor)
    ),
    secondaryGradient = Brush.linearGradient(
      colors = listOf(thirdColor, fourthColor)
    ),
    shadowColor = firstColor
  )
}

object CardLightGradients {
  val gradients = listOf(
    Gradient(
      firstColor = Color(0xFFEFE8A4),
      secondColor = Color(0xFFEFA984),
      thirdColor = Color(0xFFEFD984),
      fourthColor = Color(0xFFEFBFA3),
    ),
    Gradient(
      firstColor = Color(0xFFEFA4EF),
      secondColor = Color(0xFFA4A4EF),
      thirdColor = Color(0xFFD0A4EF),
      fourthColor = Color(0xFFC1B3EF),
    ),
    Gradient(
      firstColor = Color(0xFFA4EFA7),
      secondColor = Color(0xFFA4EFEF),
      thirdColor = Color(0xFFA4D6C2),
      fourthColor = Color(0xFFA4C8B0),
    ),
    Gradient(
      firstColor = Color(0xFFA4EFEF),
      secondColor = Color(0xFFA4A4EF),
      thirdColor = Color(0xFFB3B3EF),
      fourthColor = Color(0xFFB3B3EF),
    ),
    Gradient(
      firstColor = Color(0xFFEFA4C1),
      secondColor = Color(0xFFC1A4EF),
      thirdColor = Color(0xFFD6A4E1),
      fourthColor = Color(0xFFEFA4B9),
    )
  )
}

object CardDarkGradients {
  val gradients = listOf(
    Gradient(
      firstColor = Color(0xFF6B7280),
      secondColor = Color(0xFF374151),
      thirdColor = Color(0xFF4B5563),
      fourthColor = Color(0xFF6B7280),
    ),
    Gradient(
      firstColor = Color(0xFF4B5563),
      secondColor = Color(0xFF6B7280),
      thirdColor = Color(0xFF4B5563),
      fourthColor = Color(0xFF6B7280),
    ),
    Gradient(
      firstColor = Color(0xFF374151),
      secondColor = Color(0xFF4B5563),
      thirdColor = Color(0xFF6B7280),
      fourthColor = Color(0xFF4B5563),
    ),
    Gradient(
      firstColor = Color(0xFF4B5563),
      secondColor = Color(0xFF374151),
      thirdColor = Color(0xFF6B7280),
      fourthColor = Color(0xFF4B5563),
    ),
    Gradient(
      firstColor = Color(0xFF6B7280),
      secondColor = Color(0xFF4B5563),
      thirdColor = Color(0xFF374151),
      fourthColor = Color(0xFF6B7280),
    )
  )
}
