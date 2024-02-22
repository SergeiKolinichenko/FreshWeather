package info.sergeikolinichenko.data.network.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

/** Created by Sergei Kolinichenko on 21.02.2024 at 19:53 (GMT+3) **/

object ApiFactory {

  private const val BASE_URL = "https://api.weatherapi.com/v1/"

  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  val apiService: ApiService = retrofit.create()
}