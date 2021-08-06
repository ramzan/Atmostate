package ca.ramzan.atmostate.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDatabaseDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertCurrent(current: DbCurrent)

    @Insert(onConflict = REPLACE)
    suspend fun insertHourly(hourlies: List<DbHourly>)

    @Insert(onConflict = REPLACE)
    suspend fun insertDaily(dailies: List<DbDaily>)

    @Insert(onConflict = REPLACE)
    suspend fun insertAlerts(alert: List<DbAlert>)


    @Query("SELECT * FROM current_table")
    fun getCurrentForecast(): Flow<DbCurrent?>

    @Query("SELECT * FROM hourly_table")
    fun getHourlyForecast(): Flow<List<DbHourly>>

    @Query("SELECT * FROM daily_table")
    fun getDailyForecast(): Flow<List<DbDaily>>

    @Query("SELECT * FROM alert_table")
    fun getAlerts(): Flow<List<DbAlert>>
}