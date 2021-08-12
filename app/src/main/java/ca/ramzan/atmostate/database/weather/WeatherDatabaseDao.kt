package ca.ramzan.atmostate.database.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDatabaseDao {
    @Insert
    suspend fun insertCurrent(current: DbCurrent)

    @Insert
    suspend fun insertHourly(hourlies: List<DbHourly>)

    @Insert
    suspend fun insertDaily(dailies: List<DbDaily>)

    @Insert
    suspend fun insertAlerts(alert: List<DbAlert>)


    @Query(
        """
        SELECT * FROM current_table 
        WHERE cityId = (SELECT id FROM saved_cities WHERE selected)
    """
    )
    fun getCurrentForecast(): Flow<DbCurrent?>

    @Query(
        """
        SELECT * FROM hourly_table 
        WHERE cityId = (SELECT id FROM saved_cities WHERE selected)
    """
    )
    fun getHourlyForecast(): Flow<List<DbHourly>>

    @Query(
        """
        SELECT * FROM daily_table 
        WHERE cityId = (SELECT id FROM saved_cities WHERE selected)
    """
    )
    fun getDailyForecast(): Flow<List<DbDaily>>

    @Query(
        """
        SELECT * FROM alert_table 
        WHERE cityId = (SELECT id FROM saved_cities WHERE selected)
    """
    )
    fun getAlerts(): Flow<List<DbAlert>>

    @Transaction
    suspend fun saveForecast(
        cityId: Long,
        current: DbCurrent,
        hourly: List<DbHourly>,
        daily: List<DbDaily>,
        alerts: List<DbAlert>
    ) {
        clearCurrentForecast(cityId)
        clearHourlyForecast(cityId)
        clearDailyForecast(cityId)
        clearAlerts(cityId)
        insertCurrent(current)
        insertHourly(hourly)
        insertDaily(daily)
        insertAlerts(alerts)
    }

    @Query("DELETE FROM current_table WHERE cityId = :cityId")
    fun clearCurrentForecast(cityId: Long)

    @Query("DELETE FROM hourly_table WHERE cityId = :cityId")
    fun clearHourlyForecast(cityId: Long)

    @Query("DELETE FROM daily_table WHERE cityId = :cityId")
    fun clearDailyForecast(cityId: Long)

    @Query("DELETE FROM alert_table WHERE cityId = :cityId")
    fun clearAlerts(cityId: Long)

    @Query("SELECT date from current_table WHERE cityId = :cityId")
    suspend fun lastUpdated(cityId: Long): Long?
}