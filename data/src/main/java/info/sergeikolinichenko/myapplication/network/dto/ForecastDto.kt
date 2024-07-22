package info.sergeikolinichenko.myapplication.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 20:24 (GMT+3) **/
// This class is used to parse the JSON response from the server
// returned by method getWeatherForecast in WeatherApiService
data class ForecastDto(
  @SerializedName("location")
  val location: ForecastLocationDto,

  @SerializedName("current")
  val current: ForecastCurrentDto,

  @SerializedName("forecast")
  val forecast: ForecastDaysDto
)

data class ForecastLocationDto(
  @SerializedName("tz_id")
  val tzId: String
)

data class ForecastCurrentDto(
  @SerializedName("last_updated_epoch")
  val lastUpdatedEpoch: Long,

  @SerializedName("temp_c")
  val tempC: Float,

  @SerializedName("feelslike_c")
  val feelsLikeC: Float,

  @SerializedName("wind_kph")
  val windKph: Float,

  @SerializedName("wind_dir")
  val windDir: String,

  @SerializedName("pressure_mb")
  val pressureMb: Float,

  @SerializedName("precip_mm")
  val precipMm: Float,

  @SerializedName("cloud")
  val cloud: Int,

  @SerializedName("uv")
  val uv: Float,

  @SerializedName("humidity")
  val humidity: Int,

  @SerializedName("condition")
  val condition: ConditionDto
)

data class ForecastDaysDto(
  @SerializedName("forecastday")
  val forecastDay: List<ForecastDailyDto>,
)

data class ForecastDailyDto(
  @SerializedName("date_epoch")
  val dateEpoch: Long,

  @SerializedName("day")
  val dailyWeather: ForecastDayDto,

  @SerializedName("hour")
  val forecastHourDtoArray: List<ForecastHourDto>
)

data class ForecastDayDto(
  @SerializedName("maxtemp_c")
  val maxTempC: Float,

  @SerializedName("mintemp_c")
  val minTempC: Float,

  @SerializedName("maxwind_kph")
  val maxWindKph: Float,

  @SerializedName("uv")
  val uv: Float,

  @SerializedName("daily_will_it_rain")
  val dailyWillTtRain: Int,

  @SerializedName("daily_chance_of_rain")
  val dailyChanceOfRain: Int,

  @SerializedName("daily_will_it_snow")
  val dailyWillItSnow: Int,

  @SerializedName("daily_chance_of_snow")
  val dailyChanceOfSnow: Int,

  @SerializedName("condition")
  val conditionDto: ConditionDto
)

data class ForecastHourDto(
  @SerializedName("time_epoch")
  val timeEpoch: Long,

  @SerializedName("temp_c")
  val tempC: Float,

  @SerializedName("wind_kph")
  val windKph: Float,

  @SerializedName("pressure_mb")
  val pressureMb: Float,

  @SerializedName("humidity")
  val humidity: Int,

  @SerializedName("wind_dir")
  val windDir: String,

  @SerializedName("will_it_rain")
  val willItRain: Int,

  @SerializedName("chance_of_rain")
  val chanceOfRain: Int,

  @SerializedName("will_it_snow")
  val willItSnow: Int,

  @SerializedName("chance_of_snow")
  val chanceOfSnow: Int,

  @SerializedName("uv")
  val uv: Float,

  @SerializedName("condition")
  val condition: ConditionDto
)
