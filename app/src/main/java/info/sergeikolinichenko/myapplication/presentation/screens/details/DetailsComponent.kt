package info.sergeikolinichenko.myapplication.presentation.screens.details

import kotlinx.coroutines.flow.StateFlow

/** Created by Sergei Kolinichenko on 21.02.2024 at 15:07 (GMT+3) **/

interface DetailsComponent {

  val model: StateFlow<DetailsStore.State>
  fun onBackClicked()
  fun onChangeFavouriteStatusClicked()
}