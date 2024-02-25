package info.sergeikolinichenko.myapplication.utils

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/** Created by Sergei Kolinichenko on 25.02.2024 at 16:40 (GMT+3) **/
fun ComponentContext.componentScope() =
  CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).apply {
    lifecycle.doOnDestroy { cancel() }
  }