package ca.ramzan.atmostate.database.cities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDatabaseDao {

    @Query(
        """
        SELECT cities.id, cities.name AS city, states.name AS state, countries.name AS country
        FROM cities
        LEFT JOIN states ON cities.stateId == states.id
        LEFT JOIN countries ON cities.countryId == countries.id
    """
    )
    fun getAllCities(): Flow<List<CityName>>

    @Query(
        """
        SELECT saved_cities.id, cities.name AS city, states.name AS state, countries.name AS country
        FROM saved_cities
        LEFT JOIN cities ON saved_cities.id == cities.id
        LEFT JOIN states ON cities.stateId == states.id
        INNER JOIN countries ON cities.countryId == countries.id
    """
    )
    fun getSavedCities(): Flow<List<CityName>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveCity(city: SavedCity)

}
