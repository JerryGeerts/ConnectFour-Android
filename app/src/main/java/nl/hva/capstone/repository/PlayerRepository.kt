package nl.hva.capstone.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.withTimeout
import nl.hva.capstone.models.Player
import java.util.*

class PlayerRepository {
    private var firebaseDatabase = Firebase.database
    private val firebaseAuth = Firebase.auth
    private val userId = firebaseAuth.currentUser?.uid

    val player: MutableLiveData<Player> = MutableLiveData()
    val currentPlayer: MutableLiveData<Player> = MutableLiveData()
    val players: MutableLiveData<ArrayList<Player>> = MutableLiveData()

    suspend fun getPlayer(playerId: String) {
        try {
            withTimeout(10000) {
                firebaseDatabase.getReference("users").child(playerId).addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            println(error.message)
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val res = snapshot.getValue<Player>()
                            if (res?.id == userId) currentPlayer.value = res
                            player.value = res
                        }
                    })
            }
        } catch (error: Exception) {
            println(error.message)
            throw error
        }
    }

    suspend fun addPlayer(player: Player) {
        try {
            withTimeout(10000) {
                firebaseDatabase.getReference("users").child(player.id).setValue(player)
                    .addOnSuccessListener {
                        println("Success!")
                    }.addOnFailureListener { throw it }
            }
        } catch (error: Exception) {
            throw error
        }
    }

    suspend fun getPlayers(playerIds: ArrayList<String>) {
        try {
            withTimeout(10000) {
                firebaseDatabase.getReference("users").addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            println(error.message)
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val res = snapshot.getValue<HashMap<String, Player>>()
                            val result = res?.filter { playerIds.contains(it.key) }

                            players.value =
                                if (result?.values != null) ArrayList(result.values) else null
                        }
                    })
            }
        } catch (error: Exception) {
            throw error
        }
    }
}