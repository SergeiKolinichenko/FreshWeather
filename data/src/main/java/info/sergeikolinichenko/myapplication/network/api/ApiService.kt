package info.sergeikolinichenko.myapplication.network.api

import info.sergeikolinichenko.myapplication.network.dto.ForecastDto
import info.sergeikolinichenko.myapplication.network.dto.FoundDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Created by Sergei Kolinichenko on 21.02.2024 at 19:52 (GMT+3) **/

interface ApiService {

  @GET("search")
  suspend fun search(
    @Query("text") query: String,
    @Query("format") format: String = "json",
    @Query("limit") limit: Int = LIMIT_ON_THE_NUMBER_OF_LOCATIONS_FOUND,
  ): Response<FoundDto>

  @GET("{location}/next{days}days?unitGroup=metric&iconSet=icons2&elements=datetime,datetimeEpoch,name,cityAddressDto,resolvedAddress,latitude,longitude,tempmax,tempmin,temp,feelslike,humidity,precip,precipprob,preciptype,windspeed,winddir,pressure,uvindex,moonphase,sunriseEpoch,sunsetEpoch,moonriseEpoch,moonsetEpoch,conditions,description,icon,cloudcover&include=current,days,hours,fcst")
  suspend fun getCurrentWeather(
    @Path("location") location: String,
    @Path("days") days: String,
  ): Response<ForecastDto>

  companion object {
    private const val LIMIT_ON_THE_NUMBER_OF_LOCATIONS_FOUND = 10
  }

}