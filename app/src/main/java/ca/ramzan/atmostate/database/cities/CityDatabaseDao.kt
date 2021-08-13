package ca.ramzan.atmostate.database.cities

import androidx.room.*
import ca.ramzan.atmostate.domain.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDatabaseDao {

    @Query(
        """
        SELECT cities.id, cities.name AS city, states.name AS state, countries.name AS country
        FROM cities
        LEFT JOIN states ON cities.stateId == states.id
        LEFT JOIN countries ON cities.countryId == countries.id
        ORDER BY cities.name COLLATE NOCASE
    """
    )
    fun getAllCities(): Flow<List<DbCityName>>

    @Query(
        """
        SELECT saved_cities.id, cities.name AS city, states.name AS state, countries.name AS country, selected
        FROM saved_cities
        LEFT JOIN cities ON saved_cities.id == cities.id
        LEFT JOIN states ON cities.stateId == states.id
        LEFT JOIN countries ON cities.countryId == countries.id
        ORDER BY cities.name COLLATE NOCASE
    """
    )
    fun getSavedCities(): Flow<List<DbSavedCityName>>

    @Query("SELECT id FROM saved_cities")
    suspend fun getSavedCityIds(): List<Long>

    @Query("SELECT id FROM saved_cities")
    fun getSavedCityIdsFlow(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(city: DbSavedCity)

    @Query("SELECT * FROM saved_cities WHERE selected")
    suspend fun getSelectedCity(): DbSavedCity?

    @Query(
        """
        SELECT saved_cities.id, name 
        FROM saved_cities 
        JOIN cities ON saved_cities.id == cities.id
        WHERE selected"""
    )
    fun getSelectedCityFlow(): Flow<City?>

    @Transaction
    suspend fun selectCity(city: DbSavedCity) {
        getSelectedCity()?.run {
            insert(copy(selected = false))
        }
        insert(city.copy(selected = true))
    }

    @Query("SELECT lat, lon from cities where id = :cityId")
    suspend fun getCoordinates(cityId: Long): DbCoord

    @Query("SELECT * FROM saved_cities WHERE id == :id")
    suspend fun getCity(id: Long): DbSavedCity

    @Query("SELECT * FROM saved_cities WHERE id = :id")
    suspend fun isCitySaved(id: Long): DbSavedCity?

    @Delete
    suspend fun delete(savedCity: DbSavedCity)

    @Query("SELECT * FROM saved_cities LIMIT 1")
    suspend fun getAnySavedCity(): DbSavedCity?

    @Transaction
    suspend fun removeCity(id: Long) {
        isCitySaved(id)?.let { oldCity ->
            delete(oldCity)
            getAnySavedCity()?.let { newCity ->
                selectCity(newCity)
            }
        }
    }
}
