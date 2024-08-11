package info.sergeikolinichenko.myapplication.utils

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.store.StoreFactory
import info.sergeikolinichenko.domain.entity.City
import info.sergeikolinichenko.domain.entity.HourForecast
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.entity.ChartsHourlyScreenValues
import info.sergeikolinichenko.myapplication.entity.CityForScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 25.02.2024 at 16:40 (GMT+3) **/

internal fun ComponentContext.componentScope() =
  CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    .apply { lifecycle.doOnDestroy { cancel() } }

internal fun Float.toRoundToIntToString(): String = "${this.roundToInt()}"

internal fun Float.toPerCentFromFloat(): String = "${this.roundToInt()}%"

internal fun Int.toPerCentFromInt(): String = "$this%"

internal fun convertLongToCalendarWithTz(timeInMillis: Long, zone: String): Calendar {
  val tz = TimeZone.getTimeZone(zone)
  val calendar = Calendar.getInstance(tz)
  calendar.timeInMillis = timeInMillis * 1000
  return calendar
}

internal fun Int.toUvToStringId() = when {
  this < 3 -> R.string.details_content_text_designation_of_uv_index_low
  this in 3..5 -> R.string.details_content_text_designation_of_uv_index_moderate
  this in 6..7 -> R.string.details_content_text_designation_of_uv_index_high
  this in 8..<10 -> R.string.details_content_text_designation_of_uv_index_very_high
  this > 10 -> R.string.details_content_text_designation_of_uv_index_extreme
  else -> R.string.details_content_nothing
}

internal fun Calendar.formattedFullDate(): String {
  val format = SimpleDateFormat("EEEE ⁞ d MMM y ⁞ HH:mm", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}

internal fun Calendar.formattedShortDayOfWeek(): String {
  val format = SimpleDateFormat("EEE ⁞ dd.MM", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}

internal fun Calendar.formattedOnlyMonth(): String {
  val format = SimpleDateFormat("dd.MM", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}

internal fun Calendar.formattedFullHour(): String {
  val format = SimpleDateFormat("HH:mm", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}

internal fun Calendar.formattedShortHour(): String {
  val format = SimpleDateFormat("HH:m", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}

internal fun Calendar.formattedDayAndNameOfTheMonth(): String {
  val format = SimpleDateFormat("d MMMM", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}

internal fun Calendar.formattedDayOfWeek(): String {
  val format = SimpleDateFormat("EEEE", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  val string = format.format(time)
  return string.replaceFirstChar { it.titlecase(Locale.getDefault()) }
}

internal fun Calendar.formattedDateOfWeek(): String {
  val format = SimpleDateFormat("d MMMM", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}

internal fun String.to24HourString(): String {

  return if (this.contains("No ")) {
    "--"
  } else {
    val inputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
    val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val time = LocalTime.parse(this, inputFormatter)
    time.format(outputFormatter)
  }
}

internal fun String.toTitleMoonRise(): Boolean {
  return if (this.contains("No ")) {
    false
  } else {
    val amPm = this.substring(this.length - 2)
    return amPm == "AM"
  }
}

internal fun isTodayOrTomorrow(timeEpoch: Long, timeZoneId: String): Int {
  val now = LocalDate.now(ZoneId.of(timeZoneId))
  val date = Instant.ofEpochSecond(timeEpoch).atZone(ZoneId.of(timeZoneId)).toLocalDate()

  return when {
    date == now -> R.string.details_content_tittle_sun_moon_block_today
    date == now.plusDays(1) -> R.string.details_content_tittle_sun_moon_block_tomorrow
    else -> R.string.details_content_nothing
  }
}

internal fun toTimeDurationInSky(epoch1: Long, epoch2: Long, context: Context) = run {
  val duration = Duration.between(Instant.ofEpochSecond(epoch1), Instant.ofEpochSecond(epoch2))
  val hours = duration.toHours()
  val minutes =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) duration.toMinutesPart().toLong()
    else duration.toMinutes() - hours * 60
  context.getString(R.string.details_content_string_time_duration_h_min, hours, minutes)
}

internal fun Float.toPhaseOfMoonStringId() = when {
  this == 0f -> R.string.details_content_text_phase_of_moon_new_moon
  this > 0f && this < 0.25f -> R.string.details_content_text_phase_of_moon_waxing_crescent
  this == 0.25f -> R.string.details_content_text_phase_of_moon_first_quarter
  this > 0.25f && this < 0.5f -> R.string.details_content_text_phase_of_moon_waxing_gibbous
  this == 0.5f -> R.string.details_content_text_phase_of_moon_full_moon
  this > 0.5f && this < 0.75f -> R.string.details_content_text_phase_of_moon_waning_gibbous
  this == 0.75f -> R.string.details_content_text_phase_of_moon_third_quarter
  this > 0.75 && this <= 1 -> R.string.details_content_text_phase_of_moon_waning_crescent
  else -> R.string.details_content_nothing
}

internal fun Float.toWindDirection() = when {
  this in 0.0..11.25 -> R.string.details_content_text_wind_direction_north
  this > 11.25 && this <= 33.75 -> R.string.details_content_text_wind_direction_north_northeast
  this > 33.75 && this <= 56.25 -> R.string.details_content_text_wind_direction_northeast
  this > 56.25 && this <= 78.75 -> R.string.details_content_text_wind_direction_east_northeast
  this > 78.75 && this <= 101.25 -> R.string.details_content_text_wind_direction_east
  this > 101.25 && this <= 123.75 -> R.string.details_content_text_wind_direction_east_southeast
  this > 123.75 && this <= 146.25 -> R.string.details_content_text_wind_direction_southeast
  this > 146.25 && this <= 168.75 -> R.string.details_content_text_wind_direction_south_southeast
  this > 168.75 && this <= 191.25 -> R.string.details_content_text_wind_direction_south
  this > 191.25 && this <= 213.75 -> R.string.details_content_text_wind_direction_south_southwest
  this > 213.75 && this <= 236.25 -> R.string.details_content_text_wind_direction_southwest
  this > 236.25 && this <= 258.75 -> R.string.details_content_text_wind_direction_west_southwest
  this > 258.75 && this <= 281.25 -> R.string.details_content_text_wind_direction_west
  this > 281.25 && this <= 303.75 -> R.string.details_content_text_wind_direction_west_northwest
  this > 303.75 && this <= 326.25 -> R.string.details_content_text_wind_direction_northwest
  this > 326.25 && this <= 348.75 -> R.string.details_content_text_wind_direction_north_northwest
  this > 348.75 -> R.string.details_content_text_wind_direction_north
  else -> -R.string.details_content_nothing
}

internal fun City.toCityScreen() = CityForScreen(
  id = id,
  name = name,
  region = region,
  country = country,
  lat = lat,
  lon = lon
)

internal fun CityForScreen.toCity() = City(
  id = id,
  name = name,
  region = region,
  country = country,
  lat = lat,
  lon = lon
)

private fun HourForecast.toChartsHourlyScreenValues() = ChartsHourlyScreenValues(
  date = date,
  pressureFloat = pressure.substringBeforeLast('\n').toFloat(),
  pressureString = pressure,
  humidity = humidity.toInt(),
  uv = uvIndex.toFloat(),
  icon = icon
)

internal fun List<HourForecast>.toListChartsHourlyScreenValues() =
  map { it.toChartsHourlyScreenValues() }

internal fun Float.fromKphToStringId() = when {
  this < 0.2 -> R.string.beaufort_scale_calm
  this > 0.2 && this <= 1.5 -> R.string.beaufort_scale_light_air
  this > 1.5 && this <= 3.3 -> R.string.beaufort_scale_light_breeze
  this > 3.3 && this <= 5.4 -> R.string.beaufort_scale_gentle_breeze
  this > 5.4 && this <= 7.9 -> R.string.beaufort_scale_moderate_breeze
  this > 7.9 && this <= 10.7 -> R.string.beaufort_scale_fresh_breeze
  this > 10.7 && this <= 13.8 -> R.string.beaufort_scale_strong_breeze
  this > 13.8 && this <= 17.1 -> R.string.beaufort_scale_moderate_gale
  this > 17.1 && this <= 20.7 -> R.string.beaufort_scale_fresh_gale
  this > 20.7 && this <= 24.4 -> R.string.beaufort_scale_strong_gale
  this > 24.4 && this <= 28.4 -> R.string.beaufort_scale_whole_gale
  this > 28.4 && this <= 32.6 -> R.string.beaufort_scale_storm
  this > 32.6 -> R.string.beaufort_scale_hurricane
  else -> -1
}

internal fun String.toDegree() = when {
  this == "N" -> 0f
  this == "NNE" -> 22.5f
  this == "NE" -> 45f
  this == "ENE" -> 67.5f
  this == "E" -> 90f
  this == "ESE" -> 112.5f
  this == "SE" -> 135f
  this == "SSE" -> 157.5f
  this == "S" -> 180f
  this == "SSW" -> 202.5f
  this == "SW" -> 225f
  this == "WSW" -> 247.5f
  this == "W" -> 270f
  this == "WNW" -> 292.5f
  this == "NW" -> 315f
  this == "NNW" -> 337.5f
  else -> 0f
}

internal fun toPrecipitationTypeString(context: Context, list: List<String>?): String? {

  if (list.isNullOrEmpty()) return null // Handle null or empty list early

  val stringResources = mapOf(
    "rain" to R.string.details_content_rain,"snow" to R.string.details_content_snow,
    "freezingrain" to R.string.details_content_freezingrain,
    "ice" to R.string.details_content_ice,
    "nothing" to R.string.details_content_nothing
  )

  return list.joinToString("\n") { item ->
    context.getString(stringResources[item] ?: R.string.details_content_nothing)
  }
}


internal fun String.toIconId() = when {
  this == "rain-snow" -> R.mipmap.ic_rain_snow
  this == "snow" -> R.mipmap.ic_snow
  this == "rain" -> R.mipmap.ic_rain
  this == "fog" -> R.mipmap.ic_fog
  this == "wind" -> R.mipmap.ic_wind
  this == "cloudy" -> R.mipmap.ic_cloudy
  this == "partly-cloudy-day" -> R.mipmap.ic_partly_cloudy_day
  this == "partly-cloudy-night" -> R.mipmap.ic_partly_cloudy_night
  this == "rain-snow-showers-day" -> R.mipmap.ic_rain_snow_showers_day
  this == "rain-snow-showers-night" -> R.mipmap.ic_rain_snow_showers_night
  this == "rain-sleet-showers-dayrain-snow" -> R.mipmap.ic_rain_snow
  this == "showers-day" -> R.mipmap.ic_showers_day
  this == "showers-night" -> R.mipmap.ic_showers_night
  this == "sleet" -> R.mipmap.ic_sleet
  this == "snow-showers-day" -> R.mipmap.ic_snow_showers_day
  this == "snow-showers-night" -> R.mipmap.ic_snow_showers_night
  this == "clear-day" -> R.mipmap.ic_clear_day
  this == "clear-night" -> R.mipmap.ic_clear_night
  this == "thunder" -> R.mipmap.ic_thunder
  this == "thunder-rain" -> R.mipmap.ic_thunder_rain
  this == "thunder-showers-day" -> R.mipmap.ic_thunder_showers_day
  this == "thunder-showers-night" -> R.mipmap.ic_thunder_showers_night
  this == "hail" -> R.mipmap.ic_hail
  else -> R.mipmap.ic_clear_day
}

@Composable
internal fun Dp.toSp() = with(LocalDensity.current) { this@toSp.toSp() }

