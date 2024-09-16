package info.sergeikolinichenko.myapplication.network.api

import info.sergeikolinichenko.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.Locale

/** Created by Sergei Kolinichenko on 21.02.2024 at 19:53 (GMT+3) **/

object ApiFactory {

  private const val BASE_URL_VIS =
    "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
  private const val BASE_URL_OSM = "https://nominatim.openstreetmap.org/"
  private const val PARAM_KEY = "key"
  private const val PARAM_LANG = "lang"
  private const val PARAM_ACCEPT_LANG = "accept-language"

  // for UI test so created at Mockoon
  private const val TEST_BASE_URL = "http://10.0.2.2:3000/"

  private val logging = HttpLoggingInterceptor()

  init {
    logging.level = HttpLoggingInterceptor.Level.BODY
  }

  private val okHttpClientOsm = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
      val originalRequest = chain.request()
      val originalHttpUrl = originalRequest.url
      val url = originalHttpUrl.newBuilder()
        .addQueryParameter(PARAM_ACCEPT_LANG, Locale.getDefault().language)
        .build()
      val desiredRequest = originalRequest.newBuilder()
        .url(url)
        .build()
      chain.proceed(desiredRequest)
    }
    .build()

  private val okHttpClientVis = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
      val originalRequest = chain.request()
      val originalHttpUrl = originalRequest.url
      val url = originalHttpUrl.newBuilder()
        .addQueryParameter(PARAM_KEY, BuildConfig.API_KEY_VISUALCROSSING)
        .addQueryParameter(PARAM_LANG, Locale.getDefault().language)
        .build()
      val desiredRequest = originalRequest.newBuilder()
        .url(url)
        .build()
      chain.proceed(desiredRequest)
    }
    .build()

  var apiServiceOpenStreetMap = Retrofit.Builder()
    .baseUrl(BASE_URL_OSM)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClientOsm)
    .build()
    .create<ApiService>()

  var apiServiceForVisualcrossing = Retrofit.Builder()
    .baseUrl(BASE_URL_VIS)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClientVis)
    .build()
    .create<ApiService>()
}