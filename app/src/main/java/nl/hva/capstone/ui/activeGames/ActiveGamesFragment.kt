package nl.hva.capstone.ui.activeGames

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_active_games.*
import nl.hva.capstone.R
import nl.hva.capstone.misc.navigateSafe
import nl.hva.capstone.models.Game
import nl.hva.capstone.models.GameStatus
import nl.hva.capstone.ui.activeGames.adapter.ActiveGameAdapter
import nl.hva.capstone.ui.activeGames.viewModels.ActiveGamesViewModel
import nl.hva.capstone.ui.game.GameFragment

class ActiveGamesFragment : Fragment() {

    private val auth: FirebaseAuth = Firebase.auth
    private val activeGamesViewModel: ActiveGamesViewModel by activityViewModels()

    private lateinit var activeGameAdapter: ActiveGameAdapter

    private val games = arrayListOf<Game>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_active_games, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        activeGameAdapter = ActiveGameAdapter(games, this::joinGame)
        rv_active_games.adapter = activeGameAdapter
        rv_active_games.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        initObserver()
    }

    private fun initObserver() {
        activeGamesViewModel.getGames()
        activeGamesViewModel.games.observe(viewLifecycleOwner) { allGames ->
            games.clear()

            allGames?.filter {
                it.players.firstOrNull { player -> player.id == auth.currentUser?.uid } != null && it.status == GameStatus.STARTED
            }?.toList()?.let { games.addAll(it) }

            activeGameAdapter.notifyDataSetChanged()
        }
    }

    private fun joinGame(game: Game) {
        activeGamesViewModel.joinGame(game) { updatedGame: Game, joined: Boolean ->
            if (joined) navigateSafe(
                R.id.action_active_games_to_game,
                bundleOf("title" to updatedGame.name, GameFragment.GAME_TAG to updatedGame)
            )
            else AlertDialog.Builder(context).setTitle("Couldn't join game")
                .setMessage("There was a error joining the game, try again later.")
                .setPositiveButton("Continue") { _: DialogInterface, _: Int -> }
                .show()
        }
    }
}