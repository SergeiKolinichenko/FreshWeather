package info.sergeikolinichenko.myapplication.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** Created by Sergei Kolinichenko on 18.07.2024 at 17:22 (GMT+3) **/

fun <T> Flow<T>.test(): MutableList<T> {
  val list = ArrayList<T>()
  @Suppress("OPT_IN_USAGE")
  GlobalScope.launch(Dispatchers.Unconfined) { collect { list += it } }
  return list
}