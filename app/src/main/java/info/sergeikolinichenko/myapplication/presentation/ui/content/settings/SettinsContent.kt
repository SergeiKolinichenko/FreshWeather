package info.sergeikolinichenko.myapplication.presentation.ui.content.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.sergeikolinichenko.domain.entity.PRECIPITATION
import info.sergeikolinichenko.domain.entity.PRESSURE
import info.sergeikolinichenko.domain.entity.TEMPERATURE
import info.sergeikolinichenko.myapplication.R
import info.sergeikolinichenko.myapplication.presentation.components.settings.SettingsComponent
import info.sergeikolinichenko.myapplication.utils.ResponsiveText

/** Created by Sergei Kolinichenko on 14.07.2024 at 18:16 (GMT+3) **/

@Composable
fun SettingsContent(component: SettingsComponent) {

  Box( // it's for background
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    SettingsScreen(
      modifier = Modifier,
      component = component
    )
  }
}

@Composable
private fun SettingsScreen(
  modifier: Modifier = Modifier,
  component: SettingsComponent,
) {

  val state = component.model.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
      .verticalScroll(rememberScrollState())
      .background(MaterialTheme.colorScheme.background),
    horizontalAlignment = Alignment.Start
  ) {
    // Header
    ScreenTopBar(
      modifier = Modifier.align(Alignment.Start),
      onClickedBack = { component.onClickedBack() },
      onClickedSaveSettings = { component.onClickedSaveSettings() }
    )
    // Block units
    // Unit Temperature

    val temperatureButtonClicked = when (state.value.temperature) {
      TEMPERATURE.CELSIUS -> RadioButtonClicked.TOP_BUTTON_CLICKED
      TEMPERATURE.FAHRENHEIT -> RadioButtonClicked.BOTTOM_BUTTON_CLICKED
    }

    RadioButtonsUnit(
      radioButtonClicked = temperatureButtonClicked,
      unitTitle = R.string.settings_content_unit_title_temperature,
      topButtonTitle = R.string.settings_content_unit_title_temperature_celsius,
      bottomButtonTitle = R.string.settings_content_unit_title_temperature_fahrenheit,
      onClickTopButton = { component.setTemperaturesType(type = TEMPERATURE.CELSIUS) },
      onClickBottomButton = { component.setTemperaturesType(type = TEMPERATURE.FAHRENHEIT) }
    )
    // Unit Precipitation

    val precipitationButtonClicked = when (state.value.precipitation) {
      PRECIPITATION.INCHES -> RadioButtonClicked.TOP_BUTTON_CLICKED
      PRECIPITATION.MM -> RadioButtonClicked.BOTTOM_BUTTON_CLICKED
    }

    RadioButtonsUnit(
      radioButtonClicked = precipitationButtonClicked,
      unitTitle = R.string.settings_content_unit_title_precipitation,
      topButtonTitle = R.string.settings_content_unit_title_precipitation_inches,
      bottomButtonTitle = R.string.settings_content_unit_title_precipitation_millimeters,
      onClickTopButton = { component.setPrecipitationType(type = PRECIPITATION.INCHES) },
      onClickBottomButton = { component.setPrecipitationType(type = PRECIPITATION.MM) }
    )
    // Unit Pressure

    val pressureButtonClicked = when (state.value.pressure) {
      PRESSURE.MMHG -> RadioButtonClicked.TOP_BUTTON_CLICKED
      PRESSURE.HPA -> RadioButtonClicked.BOTTOM_BUTTON_CLICKED
    }

    RadioButtonsUnit(
      radioButtonClicked = pressureButtonClicked,
      unitTitle = R.string.settings_content_unit_title_pressure,
      topButtonTitle = R.string.settings_content_unit_title_pressure_mmhg,
      bottomButtonTitle = R.string.settings_content_unit_title_pressure_hpa,
      onClickTopButton = { component.setPressureType(type = PRESSURE.MMHG) },
      onClickBottomButton = { component.setPressureType(type = PRESSURE.HPA) }
    )
    // Unit Quantity
    DaysOfWeather(
      state = state,
      daysOfWeather = { component.setDaysOfWeather(days = it) }
    )
    // Unit Evaluate the application
    WideButton(
      textId = R.string.settings_content_unit_evaluate_the_application
    ) { context ->
      component.onClickedEvaluateApp(context)
    }

    Spacer(
      modifier = Modifier
        .height(2.dp)
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.tertiary)
    )
    WideButton(
      textId = R.string.settings_content_unit_write_to_the_developers
    ) { context ->
      component.onClickedWriteDevelopers(context)
    }
  }
}

@Composable
private fun ScreenTopBar(
  modifier: Modifier = Modifier,
  onClickedBack: () -> Unit,
  onClickedSaveSettings: () -> Unit,
) {

  Box(
   modifier = modifier
      .fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.align(Alignment.CenterStart)
    ) {
      Icon(
        modifier = Modifier
          .size(24.dp)
          .clickable {
            onClickedBack() },
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        tint = MaterialTheme.colorScheme.surfaceTint,
        contentDescription = stringResource(id = R.string.settings_content_description_text_back_button)
      )
      ResponsiveText(
        modifier = Modifier
          .padding(start = 20.dp),
        text = stringResource(R.string.settings_content_title_settings),
        targetTextSizeHeight = 22.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1
      )
    }
    ResponsiveText(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .clickable {
          onClickedSaveSettings() },
      text = stringResource(R.string.many_place_title_button_done),
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      targetTextSizeHeight = 16.sp,
      textAlign = TextAlign.Start,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}