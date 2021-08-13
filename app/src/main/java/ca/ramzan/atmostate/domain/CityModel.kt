package ca.ramzan.atmostate.domain

data class SavedCity(
    val id: Long,
    val name: String,
    val selected: Boolean
)

data class City(
    val id: Long,
    val name: String,
)