package info.sergeikolinichenko.myapplication.presentation.ui.content.settings.nonuifuns

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import info.sergeikolinichenko.myapplication.R

/** Created by Sergei Kolinichenko on 22.07.2024 at 10:12 (GMT+3) **/

internal fun Context.writeSDevelopers(): Boolean {

  val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto: sergeikolinicenko@gmail.com")
    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_store_feedback_for_freshweather_app))
    putExtra(Intent.EXTRA_TEXT,
      getString(R.string.settings_store_dear_developers_i_have_the_following_feedback))
  }
  try {
    this.startActivity(Intent.createChooser(emailIntent, "Send feedback"))
    return true
  } catch (ex: ActivityNotFoundException) {
    return false
  }
}

internal fun Context.evaluateApp() {
  val name = this.packageName
  val uri: Uri = Uri.parse("market://details?id=$name")
  val goToMarket = Intent(Intent.ACTION_VIEW, uri)
  goToMarket.addFlags(
    Intent.FLAG_ACTIVITY_NO_HISTORY or
        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
  )
  try {
    this.startActivity(goToMarket)
  } catch (e: ActivityNotFoundException) {
    this.startActivity(
      Intent(
        Intent.ACTION_VIEW,
        Uri.parse("http://play.google.com/store/apps/details?id=$name")
      )
    )
  }
}