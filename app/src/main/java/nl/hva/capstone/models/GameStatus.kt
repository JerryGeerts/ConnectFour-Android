package nl.hva.capstone.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
enum class GameStatus(val status: String) {
    PENDING("PENDING"),
    STARTED("STARTED"),
    FINISHED("FINISHED")
}