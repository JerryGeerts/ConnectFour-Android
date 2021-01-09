package nl.hva.capstone.ui.lobby.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import nl.hva.capstone.models.Game
import nl.hva.capstone.models.GameStatus
import nl.hva.capstone.repository.GameRepository

class LobbyViewModel : ViewModel() {
    private val gameRepository = GameRepository()
    private val userId = Firebase.auth.currentUser?.uid

    val game = gameRepository.game
    val error: MutableLiveData<String> = MutableLiveData()

    fun getGame(gameId: String) {
        try {
            viewModelScope.launch {
                gameRepository.getGame(gameId)
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with retrieving these games"
            error.value = msg
        }
    }

    fun leaveGame(game: Game) {
        try {
            viewModelScope.launch {
                val player = game.players.firstOrNull { it.id == userId }
                if (player != null) {
                    game.players.remove(player)

                    if (game.players.size == 0) gameRepository.removeGame(game)
                    else {
                        if (player.owner) {
                            val otherPlayer = game.players.first()
                            otherPlayer.owner = true
                            otherPlayer.ready = true
                        }

                        gameRepository.updateGame(game)
                    }
                }
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with leaving the game"
            error.value = msg
        }
    }

    fun setReady(game: Game) {
        try {
            viewModelScope.launch {
                val player = game.players.find { it.id == userId }

                if (player != null && !player.ready) {
                    player.ready = true
                    gameRepository.updateGame(game)
                }
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong updating status to ready"
            error.value = msg
        }
    }

    fun setUnready(game: Game) {
        try {
            viewModelScope.launch {
                val player = game.players.find { it.id == userId }

                if (player != null && player.ready) {
                    player.ready = false
                    gameRepository.updateGame(game)
                }
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong updating status to ready"
            error.value = msg
        }
    }

    fun startGame(game: Game) {
        try {
            viewModelScope.launch {
                game.status = GameStatus.STARTED
                game.turn = game.players.random().id
                gameRepository.updateGame(game)
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong updating status to ready"
            error.value = msg
        }
    }
}