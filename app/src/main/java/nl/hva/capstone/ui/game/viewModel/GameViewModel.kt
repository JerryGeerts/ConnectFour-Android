package nl.hva.capstone.ui.game.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import nl.hva.capstone.models.Game
import nl.hva.capstone.repository.GameRepository

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val gameRepository = GameRepository()

    val game = gameRepository.game
    val error: MutableLiveData<String> = MutableLiveData()

    fun updateGame(game: Game) {
        try {
            viewModelScope.launch {
                gameRepository.updateGame(game)
            }
        } catch (exception: Exception) {
            val msg = "Something went wrong with adding the game"
            error.value = msg
        }
    }

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
}