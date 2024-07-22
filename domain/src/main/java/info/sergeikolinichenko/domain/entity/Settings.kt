package info.sergeikolinichenko.domain.entity

/** Created by Sergei Kolinichenko on 12.07.2024 at 20:07 (GMT+3) **/

data class Settings(
  val temperature: TEMPERATURE,
  val precipitation: PRECIPITATION,
  val pressure: PRESSURE
)

enum class TEMPERATURE {
  CELSIUS,
  FAHRENHEIT
}

enum class PRECIPITATION {
  INCHES,
  MM
}

enum class PRESSURE {
  MMHG,
  HPA
}
