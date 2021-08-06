package ca.ramzan.atmostate.database

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


    @Query("SELECT * FROM current_table")
    fun getCurrentForecast(): Flow<DbCurrent?>

    @Query("SELECT * FROM hourly_table")
    fun getHourlyForecast(): Flow<List<DbHourly>>

    @Query("SELECT * FROM daily_table")
    fun getDailyForecast(): Flow<List<DbDaily>>

    @Query("SELECT * FROM alert_table")
    fun getAlerts(): Flow<List<DbAlert>>

    @Transaction
    fun clearOldForecast() {
        clearCurrentForecast()
        clearHourlyForecast()
        clearDailyForecast()
        clearAlerts()
    }

    @Query("DELETE FROM current_table")
    fun clearCurrentForecast()

    @Query("DELETE FROM hourly_table")
    fun clearHourlyForecast()

    @Query("DELETE FROM daily_table")
    fun clearDailyForecast()

    @Query("DELETE FROM alert_table")
    fun clearAlerts()
}