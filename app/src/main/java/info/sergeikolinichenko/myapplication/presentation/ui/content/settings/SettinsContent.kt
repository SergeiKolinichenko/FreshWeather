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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import info.sergeikolinichenko.myapplication.presentation.screens.settings.component.SettingsComponent

/** Created by Sergei Kolinichenko on 14.07.2024 at 18:16 (GMT+3) **/

@Composable
fun SettingsContent(component: SettingsComponent) {

  val state = component.model.collectAsState()
  val context = LocalContext.current

  Box( // it's for background
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
        .background(MaterialTheme.colorScheme.background),
      horizontalAlignment = Alignment.Start
    ) {
      // Header
      Box(
        Modifier
          .fillMaxWidth()
          .align(Alignment.Start)
      ) {
        Row(
          modifier = Modifier.align(Alignment.CenterStart)
        ) {
          Icon(
            modifier = Modifier
              .size(24.dp)
              .clickable { component.onClickedBack() },
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.content_description_text_back_button)
          )
          Text(
            modifier = Modifier
              .padding(start = 20.dp),
            text = stringResource(R.string.settings_content_title_settings),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
        Text(
          modifier = Modifier
            .align(Alignment.CenterEnd)
            .clickable { component.onClickedSaveSettings() },
          text = stringResource(R.string.settings_content_button_done),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Medium,
          fontSize = 16.sp,
          textAlign = TextAlign.Start,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
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
        unitTitle = R.string.settings_content_unit_titile_pressure,
        topButtonTitle = R.string.settings_content_unit_title_pressure_mmhg,
        bottomButtonTitle = R.string.settings_content_unit_title_pressure_hpa,
        onClickTopButton = { component.setPressureType(type = PRESSURE.MMHG) },
        onClickBottomButton = { component.setPressureType(type = PRESSURE.HPA) }
      )

      // Unit Evaluate the application

      WideButton(
        textId = R.string.settings_content_unit_evaluate_the_application
      ) {
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
      ) {
        component.onClickedWriteDevelopers(context)
      }
    }
  }
}