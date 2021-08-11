package ca.ramzan.atmostate.database.cities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val CITY_DB_NAME = "city_database"
const val CITY_DB_VERSION = 1

@Database(
    entities = [
        City::class,
        State::class,
        Country::class,
        SavedCity::class
    ],
    version = CITY_DB_VERSION,
    exportSchema = false
)
abstract class CityDatabase : RoomDatabase() {

    abstract val cityDatabaseDao: CityDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: CityDatabase? = null

        fun getInstance(context: Context): CityDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CityDatabase::class.java,
                        CITY_DB_NAME
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