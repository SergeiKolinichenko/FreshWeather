package info.sergeikolinichenko.data.network.api

import info.sergeikolinichenko.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.Locale

/** Created by Sergei Kolinichenko on 21.02.2024 at 19:53 (GMT+3) **/

object ApiFactory {

  private const val BASE_URL = "https://api.weatherapi.com/v1/"
  private const val PARAM_KEY = "key"
  private const val PARAM_LANG = "lang"

  private val logging = HttpLoggingInterceptor()
  init {
    logging.level = HttpLoggingInterceptor.Level.BODY
  }


  private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor{ chain ->
      val originalRequest = chain.request()
      val originalHttpUrl = originalRequest.url
      val url = originalHttpUrl.newBuilder()
        .addQueryParameter( PARAM_KEY, BuildConfig.API_KEY)
        .addQueryParameter(PARAM_LANG, Locale.getDefault().language)
        .build()
      val desiredRequest = originalRequest.newBuilder()
        .url(url)
        .build()
      chain.proceed(desiredRequest)
    }
    .build()

  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

  val apiService: ApiService = retrofit.create()
}