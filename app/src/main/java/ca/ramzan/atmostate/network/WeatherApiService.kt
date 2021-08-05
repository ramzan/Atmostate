package ca.ramzan.atmostate.network

import com.ramzan.atmostate.BuildConfig
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("onecall")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appId: String = BuildConfig.OWM_API_KEY,
    ): WeatherResult.Success
}

class WeatherApi(moshi: Moshi) {
    private val retrofit: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }

    suspend fun getForecast(lat: Double, lon: Double): WeatherResult {
        return try {
            retrofit.getForecast(lat, lon)
        } catch (e: Exception) {
            WeatherResult.Failure(e.toString())
        }
    }
}
