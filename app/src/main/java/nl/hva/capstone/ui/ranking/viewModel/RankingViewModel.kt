package nl.hva.capstone.ui.ranking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import nl.hva.capstone.repository.GameRepository

class RankingViewModel : ViewModel() {
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
}