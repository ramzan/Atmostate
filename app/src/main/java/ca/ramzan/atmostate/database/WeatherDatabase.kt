package ca.ramzan.atmostate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


const val DB_NAME = "weather_database"
const val DB_VERSION = 1

@Database(
    entities = [
//        Weather::class,
    ],
    version = DB_VERSION
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val weatherDatabaseDao: WeatherDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}