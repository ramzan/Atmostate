package ca.ramzan.atmostate.database.cities

import androidx.room.*
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
        LEFT JOIN countries ON cities.countryId == countries.id
    """
    )
    fun getSavedCities(): Flow<List<CityName>>

    @Query("SELECT id FROM saved_cities")
    suspend fun getSavedCityIds(): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(city: SavedCity)

    @Query("SELECT * FROM saved_cities WHERE selected")
    suspend fun getSelectedCity(): SavedCity?

    @Query(
        """
        SELECT saved_cities.id, name 
        FROM saved_cities 
        JOIN cities ON saved_cities.id == cities.id
        WHERE selected"""
    )
    fun getSelectedCityFlow(): Flow<CityDisplay?>

    @Transaction
    suspend fun selectCity(city: SavedCity) {
        getSelectedCity()?.run {
            insert(copy(selected = false))
        }
        insert(city.copy(selected = true))
    }

    @Query("SELECT lat, lon from cities where id = :cityId")
    suspend fun getCoordinates(cityId: Long): Coord

    @Query("SELECT * FROM saved_cities WHERE id == :id")
    suspend fun getCity(id: Long): SavedCity
}