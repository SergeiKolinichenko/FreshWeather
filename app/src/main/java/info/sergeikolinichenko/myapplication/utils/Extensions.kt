package info.sergeikolinichenko.myapplication.utils

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 25.02.2024 at 16:40 (GMT+3) **/
fun ComponentContext.componentScope() =
  CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).apply {
    lifecycle.doOnDestroy { cancel() }
  }
fun Float.toRoundToIntString(): String = "${this.roundToInt()}"
fun Float.toCelsiusString(): String = "${this.roundToInt()}°C"
fun Int.toHumidity(): String = "$this%"
fun Float.toRoundToInt(): String = "${this.roundToInt()}"
fun Float.toPrecipitation(): String = "${this.roundToInt()} mm"
fun Int.toPerCent(): String = "$this%"
fun Long.toCalendar(zone: String): Calendar {
  val tz = TimeZone.getTimeZone(zone)
  val calendar = Calendar.getInstance(tz)
  calendar.isLenient = false
  calendar.timeInMillis = this * 1000
  return calendar
}
fun Calendar.formattedFullDate(): String {
  val format = SimpleDateFormat("EEEE ⁞ d MMM y ⁞ HH:mm", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}
fun Calendar.formattedShortDayOfWeek(): String {
  val format = SimpleDateFormat("EEE ⁞ dd.MM", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}
fun Calendar.formattedOnlyMonth(): String {
  val format = SimpleDateFormat("dd.MM", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}
fun Calendar.formattedFullHour(): String {
  val format = SimpleDateFormat("HH:mm", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}
fun Calendar.formattedShortHour(): String {
  val format = SimpleDateFormat("HH:m", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}
fun Calendar.formattedDayAndNameOfTheMonth(): String {
  val format = SimpleDateFormat("d MMMM", Locale.getDefault())
  format.isLenient = false
  format.timeZone = this.timeZone
  return format.format(time)
}