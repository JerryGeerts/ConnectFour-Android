package nl.hva.capstone.ui.activeGames.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import nl.hva.capstone.models.Game
import nl.hva.capstone.repository.GameRepository

class ActiveGamesViewModel : ViewModel() {
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

    fun joinGame(game: Game, callback: (Game, Boolean) -> Unit) {
        try {
            viewModelScope.launch {
                val player = game.players.firstOrNull { it.id == userId }

                if (player != null) {
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