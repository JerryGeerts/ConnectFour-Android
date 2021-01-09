package nl.hva.capstone.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.capstone.models.Player
import nl.hva.capstone.repository.GameRepository
import nl.hva.capstone.repository.PlayerRepository
import java.util.*

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val playerRepository = PlayerRepository()
    private val gameRepository = GameRepository()

    val player = playerRepository.player
    val players = playerRepository.players
    val currentPlayer = playerRepository.currentPlayer

    val error: MutableLiveData<String> = MutableLiveData()

    companion object {
        private const val TAG = "PLAYER_VIEW_MODEL"
    }

    fun getPlayer(playerId: String) {
        try {
            viewModelScope.launch {
                playerRepository.getPlayer(playerId)
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with retrieving this player"
            error.value = msg
        }
    }

    fun addPlayer(player: Player) {
        try {
            viewModelScope.launch {
                playerRepository.addPlayer(player)
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with adding a player"
            error.value = msg
        }
    }

    fun getPlayers(players: ArrayList<String>) {
        try {
            viewModelScope.launch {
                playerRepository.getPlayers(players)
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with getting the players"
            error.value = msg
        }
    }
}