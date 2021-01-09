package nl.hva.capstone.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.withTimeout
import nl.hva.capstone.models.Game

class GameRepository {
    private var firebaseDatabase = Firebase.database

    val games: MutableLiveData<ArrayList<Game>?> = MutableLiveData()
    val game: MutableLiveData<Game> = MutableLiveData()

    suspend fun getGames() {
        try {
            withTimeout(10000) {
                firebaseDatabase.getReference("games").addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            println(error.message)
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val res = snapshot.getValue<HashMap<String, Game>>()
                            games.value = if (res?.values != null) ArrayList(res.values) else null
                        }
                    })
            }
        } catch (error: Exception) {
            println(error.message)
            throw error
        }
    }

    suspend fun addGame(game: Game, callback: ((Game) -> Unit)? = null) {
        try {
            withTimeout(10000) {
                val ref = firebaseDatabase.getReference("games")
                game.id = ref.push().key.toString()

                ref.child(game.id)
                    .setValue(game)
                    .addOnSuccessListener { if (callback != null) callback(game) }
                    .addOnFailureListener { throw it }
            }
        } catch (error: Exception) {
            if (callback != null) callback(game)
            throw error
        }
    }

    suspend fun getGame(gameId: String) {
        try {
            withTimeout(10000) {
                firebaseDatabase.getReference("games/$gameId").addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            println(error.message)
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val res = snapshot.getValue<Game>()
                            if (res != null) game.value = res
                        }
                    })
            }
        } catch (error: Exception) {
            println(error.message)
            throw error
        }
    }

    suspend fun removeGame(game: Game, callback: ((Boolean) -> Unit)? = null) {
        try {
            withTimeout(10000) {
                firebaseDatabase.getReference("games/${game.id}").removeValue()
                    .addOnSuccessListener { if (callback != null) callback(true) }
                    .addOnFailureListener { throw it }
            }
        } catch (error: Exception) {
            if (callback != null) callback(false)
            println(error.message)
            throw error
        }
    }

    suspend fun updateGame(game: Game, callback: ((Boolean) -> Unit)? = null) {
        try {
            withTimeout(10000) {
                firebaseDatabase.getReference("games/${game.id}").setValue(game)
                    .addOnSuccessListener { if (callback != null) callback(true) }
                    .addOnFailureListener { throw it }
            }
        } catch (error: Exception) {
            if (callback != null) callback(false)
            println(error.message)
            throw error
        }
    }
}