package info.sergeikolinichenko.myapplication.utils

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher

/** Created by Sergei Kolinichenko on 20.07.2024 at 10:38 (GMT+3) **/

internal fun withRole(role: Role) = SemanticsMatcher("${SemanticsProperties.Role.name} contains '$role'") {
  val roleProperty = it.config.getOrNull(SemanticsProperties.Role) ?: false
  roleProperty == role
}