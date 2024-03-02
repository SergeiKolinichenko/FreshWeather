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
import kotlin.math.roundToInt

/** Created by Sergei Kolinichenko on 25.02.2024 at 16:40 (GMT+3) **/
fun ComponentContext.componentScope() =
  CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).apply {
    lifecycle.doOnDestroy { cancel() }
  }
fun Float.toCelsius(): String = "${this.roundToInt()}°C"
fun Calendar.formattedFullDate(): String {
  val format = SimpleDateFormat("EEEE | d MMM y", Locale.getDefault())
  return format.format(time)
}
fun Calendar.formattedShortDayOfWeek(): String {
  val format = SimpleDateFormat("EEE", Locale.getDefault())
  return format.format(time)
}