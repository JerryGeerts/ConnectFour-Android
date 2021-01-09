package nl.hva.capstone.ui.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_lobby.*
import nl.hva.capstone.R
import nl.hva.capstone.misc.navigateSafe
import nl.hva.capstone.models.Game
import nl.hva.capstone.models.GamePlayer
import nl.hva.capstone.models.GameStatus
import nl.hva.capstone.ui.game.GameFragment
import nl.hva.capstone.ui.lobby.adapter.LobbyAdapter
import nl.hva.capstone.ui.lobby.viewModel.LobbyViewModel

class LobbyFragment : Fragment() {

    private val lobbyViewModel: LobbyViewModel by activityViewModels()
    private val auth: FirebaseAuth = Firebase.auth

    private lateinit var game: Game
    private lateinit var lobbyAdapter: LobbyAdapter

    private var players = arrayListOf<GamePlayer>()

    companion object {
        const val GAME_KEY = "GAME_KEY"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_lobby, container, false)
        game = arguments?.get(GAME_KEY) as Game
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecyclerView() {
        lobbyAdapter = LobbyAdapter(players)
        rv_players.adapter = lobbyAdapter
        rv_players.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        getGame()
    }

    private fun getGame() {
        lobbyViewModel.getGame(game.id)
        lobbyViewModel.game.observe(viewLifecycleOwner) {
            if (it.id == game.id) {
                game = it
                players.clear()

                val player = game.players.firstOrNull { it.id == auth.currentUser?.uid }

                if (player == null) exitLobby()
                else if (game.status == GameStatus.STARTED) navigateSafe(
                    R.id.action_lobby_to_game,
                    bundleOf("title" to game.name, GameFragment.GAME_TAG to game)
                )

                players.addAll(this.game.players)
                lobbyAdapter.notifyDataSetChanged()
                updateButton()
            }
        }
    }

    private fun updateButton() {
        val uid = auth.currentUser?.uid
        val player = players.firstOrNull { it.id == uid }

        if (player != null) {
            if (player.owner) {
                btn_ready_text.text = getString(R.string.lobby_start)
                btn_ready.visibility =
                    if (game.allReady() && game.players.size == 2) View.VISIBLE else View.GONE

                btn_ready.setOnClickListener {
                    lobbyViewModel.startGame(game)
                }
            } else {
                btn_ready.visibility = View.VISIBLE
                btn_ready_text.text = getString(
                    if (player.ready) R.string.lobby_unready
                    else R.string.lobby_ready
                )

                btn_ready.setOnClickListener {
                    if (uid != null) {
                        if (player.ready) lobbyViewModel.setUnready(game)
                        else lobbyViewModel.setReady(game)

                        lobbyAdapter.notifyDataSetChanged()
                        updateButton()
                    }
                }
            }
        } else exitLobby()
    }

    override fun onStop() {
        if (game.status != GameStatus.STARTED) exitLobby()
        super.onStop()
    }

    private fun exitLobby() {
        lobbyViewModel.leaveGame(game)
        findNavController().navigateUp()
    }
}