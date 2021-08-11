package ca.ramzan.atmostate.database.cities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cities",
    foreignKeys = [
        ForeignKey(
            entity = State::class,
            parentColumns = ["id"],
            childColumns = ["stateId"],
        ),
        ForeignKey(
            entity = Country::class,
            parentColumns = ["id"],
            childColumns = ["countryId"],
        )
    ],
)
data class City(
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
data class State(
    @PrimaryKey
    val id: Long,
    val name: String
)

@Entity(tableName = "countries")
data class Country(
    @PrimaryKey
    val id: Long,
    val name: String
)