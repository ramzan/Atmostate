package ca.ramzan.atmostate.database.cities

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CityDatabaseDao {

    @Query("SELECT * FROM countries")
    suspend fun getCountries(): List<Country>

    @Query("SELECT * FROM states")
    suspend fun getStates(): List<State>

    @Query("SELECT * FROM cities")
    suspend fun getCities(): List<City>
}
