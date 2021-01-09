package nl.hva.capstone.ui.gameOverview.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import nl.hva.capstone.models.Game
import nl.hva.capstone.models.GamePlayer
import nl.hva.capstone.repository.GameRepository

class GameOverviewViewModel(application: Application) : AndroidViewModel(application) {
    private val gameRepository = GameRepository()
    private val userId = Firebase.auth.currentUser?.uid

    val games = gameRepository.games
    val error: MutableLiveData<String> = MutableLiveData()

    fun getGames() {
        try {
            viewModelScope.launch {
                gameRepository.getGames()
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with retrieving the games"
            error.value = msg
        }
    }

    fun addGame(game: Game, callback: ((Game) -> Unit)? = null) {
        try {
            viewModelScope.launch {
                gameRepository.addGame(game) { if (callback != null) callback(it) }
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with adding the game"
            error.value = msg
        }
    }

    fun joinGame(game: Game, gamePlayer: GamePlayer, callback: (Game, Boolean) -> Unit) {
        try {
            viewModelScope.launch {
                val player = game.players.firstOrNull { it.id == userId }

                if (player != null || game.players.size <= 1) {
                    if (game.players.size == 0) gamePlayer.owner = true
                    if (player == null) game.players.add(gamePlayer)

                    gameRepository.updateGame(game) { callback(game, it) }
                } else callback(game, false)
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong the game"
            callback(game, false)
            error.value = msg
        }
    }
}