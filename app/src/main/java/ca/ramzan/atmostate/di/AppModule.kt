package ca.ramzan.atmostate.di

import android.app.Application
import ca.ramzan.atmostate.database.WeatherDatabase
import ca.ramzan.atmostate.database.WeatherDatabaseDao
import ca.ramzan.atmostate.network.WeatherApi
import ca.ramzan.atmostate.repository.WeatherRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun db(app: Application) = WeatherDatabase.getInstance(app)

    @Provides
    @Singleton
    fun weatherDao(db: WeatherDatabase) = db.weatherDatabaseDao

    @Provides
    @Singleton
    fun moshi() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun weatherApi(moshi: Moshi) = WeatherApi(moshi)

    @Provides
    @Singleton
    fun weatherRepo(dao: WeatherDatabaseDao, api: WeatherApi) = WeatherRepository(dao, api)

}
