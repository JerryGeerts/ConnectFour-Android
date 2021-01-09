package nl.hva.capstone.ui.gameOverview

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_game_overview.*
import nl.hva.capstone.MainActivity
import nl.hva.capstone.R
import nl.hva.capstone.misc.navigateSafe
import nl.hva.capstone.models.Game
import nl.hva.capstone.models.GamePlayer
import nl.hva.capstone.models.GameStatus
import nl.hva.capstone.ui.gameOverview.adapter.GameOverviewAdapter
import nl.hva.capstone.ui.gameOverview.viewModel.GameOverviewViewModel
import nl.hva.capstone.ui.lobby.LobbyFragment

class GameOverviewFragment : Fragment() {

    private val gameOverviewViewModel: GameOverviewViewModel by activityViewModels()

    private lateinit var gameOverviewAdapter: GameOverviewAdapter
    private lateinit var mainActivity: MainActivity

    private var games = arrayListOf<Game>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_game_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = activity as MainActivity
        initListeners()
        initRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initListeners() {
        add_game.setOnClickListener { showAddDialog() }
    }

    private fun initRecyclerView() {
        gameOverviewAdapter = GameOverviewAdapter(games, this::joinGame)
        rv_games.adapter = gameOverviewAdapter
        rv_games.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        getGames()
    }

    private fun getGames() {
        gameOverviewViewModel.getGames()
        gameOverviewViewModel.games.observe(viewLifecycleOwner) { allGames ->
            games.clear()
            allGames?.filter { it.status == GameStatus.PENDING }?.toList()?.let { games.addAll(it) }
            gameOverviewAdapter.notifyDataSetChanged()
        }
    }

    private fun joinGame(game: Game) {
        val player = mainActivity.currentPlayer

        if (player != null) {
            val gamePlayer = GamePlayer(player.id, player.name)

            gameOverviewViewModel.joinGame(game, gamePlayer) { updatedGame: Game, joined: Boolean ->
                if (joined) navigateSafe(
                    R.id.action_game_overview_to_lobby,
                    bundleOf("title" to updatedGame.name, LobbyFragment.GAME_KEY to updatedGame)
                )
                else android.app.AlertDialog.Builder(context)
                    .setTitle("Couldn't join game")
                    .setMessage("There was a error joining the game, try again later.")
                    .setPositiveButton("Continue") { _: DialogInterface, _: Int -> }
                    .show()
            }
        }
    }

    /*
    * */
    private fun addGame(game: Game) = gameOverviewViewModel.addGame(game) { joinGame(it) }

    private fun showAddDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add, null)
        val textBox = view.findViewById<EditText?>(R.id.et_name)
        val dialog = AlertDialog
            .Builder(requireContext())
            .setView(view)
            .setTitle("Add game")
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Save") { _, _ ->
                addGame(Game(textBox.text.toString(), arrayListOf()))
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        textBox.addTextChangedListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it?.isNotEmpty() ?: false
        }
    }
}