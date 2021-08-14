package ca.ramzan.atmostate.database.cities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.ramzan.atmostate.domain.CitySearchResult
import ca.ramzan.atmostate.domain.SavedCity

@Entity(
    tableName = "saved_cities",
    foreignKeys = [
        ForeignKey(
            entity = DbCity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
        )
    ],
)
data class DbSavedCity(
    @PrimaryKey
    val id: Long,
    val selected: Boolean = true
)

@Entity(
    tableName = "cities",
    foreignKeys = [
        ForeignKey(
            entity = DbState::class,
            parentColumns = ["id"],
            childColumns = ["stateId"],
        ),
        ForeignKey(
            entity = DbCountry::class,
            parentColumns = ["id"],
            childColumns = ["countryId"],
        )
    ],
)
data class DbCity(
    @PrimaryKey
    val id: Long,
    val name: String,
    @ColumnInfo(index = true)
    val stateId: Long?,
    @ColumnInfo(index = true)
    val countryId: Long?,
    val lat: Double,
    val lon: Double,
)

@Entity(tableName = "states")
data class DbState(
    @PrimaryKey
    val id: Long,
    val name: String
)

@Entity(tableName = "countries")
data class DbCountry(
    @PrimaryKey
    val id: Long,
    val name: String
)

data class DbCoord(
    val lat: Double,
    val lon: Double
)

data class DbSavedCityName(
    val id: Long,
    val city: String,
    val state: String?,
    val country: String?,
    val selected: Boolean
)

data class DbCityName(
    val id: Long,
    val city: String,
    val state: String?,
    val country: String?,
)

fun List<DbSavedCityName>.asDomainModel(): List<SavedCity> {
    return map { c ->
        SavedCity(
            c.id,
            "${c.city}${c.state?.let { ", $it" } ?: ""}${c.country?.let { ", $it" } ?: ""}",
            c.selected
        )
    }
}


@JvmName("asDomainModelDbCityName")
fun List<DbCityName>.asDomainModel(saved: List<Long>): List<CitySearchResult> {
    return map { c ->
        CitySearchResult(
            c.id,
            "${c.city}${c.state?.let { ", $it" } ?: ""}${c.country?.let { ", $it" } ?: ""}",
            saved.binarySearch(c.id) >= 0
        )
    }
}
