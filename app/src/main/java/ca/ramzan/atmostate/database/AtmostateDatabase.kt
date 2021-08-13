package ca.ramzan.atmostate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.ramzan.atmostate.database.cities.*
import ca.ramzan.atmostate.database.weather.*


const val DB_NAME = "atmostate_database"
const val DB_VERSION = 1

@Database(
    entities = [
        DbCurrent::class,
        DbHourly::class,
        DbDaily::class,
        DbAlert::class,
        DbCity::class,
        DbState::class,
        DbCountry::class,
        DbSavedCity::class
    ],
    version = DB_VERSION,
    exportSchema = false
)
abstract class AtmostateDatabase : RoomDatabase() {

    abstract val weatherDatabaseDao: WeatherDatabaseDao
    abstract val cityDatabaseDao: CityDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: AtmostateDatabase? = null

        fun getInstance(context: Context): AtmostateDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AtmostateDatabase::class.java,
                        DB_NAME
                    )
                        .createFromAsset("cities.db")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}