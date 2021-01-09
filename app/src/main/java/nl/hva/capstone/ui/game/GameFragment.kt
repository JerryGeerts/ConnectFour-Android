package nl.hva.capstone.ui.game

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.game_row.view.*
import nl.hva.capstone.R
import nl.hva.capstone.misc.navigateSafe
import nl.hva.capstone.models.Game
import nl.hva.capstone.models.GamePlayer
import nl.hva.capstone.models.GameStatus
import nl.hva.capstone.ui.game.viewModel.GameViewModel
import nl.hva.capstone.ui.lobby.LobbyFragment
import java.util.*
import kotlin.math.roundToInt

/**
 */
class GameFragment : Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()

    private lateinit var game: Game
    private lateinit var currentPlayer: GamePlayer
    private lateinit var otherPlayer: GamePlayer

    private val auth: FirebaseAuth = Firebase.auth
    private val rows = arrayListOf<View>()
    private val buttons = arrayListOf<View>()

    companion object {
        const val GAME_TAG = "GAME_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        game = arguments?.get(GAME_TAG) as Game
        currentPlayer = game.players.first { it.id == auth.currentUser?.uid }
        otherPlayer = game.players.first { it.id != auth.currentUser?.uid }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        (0..5).forEach { _ ->
            val row = View.inflate(context, R.layout.game_row, null)
            rows.add(row)
            row_container.addView(row)
        }

        (0..6).forEach { index ->
            val button = View.inflate(context, R.layout.game_button, null) as ImageView
            buttons.add(button)
            button.setOnClickListener { insertCoin(index) }
            button_bar.addView(button)
        }

        startGame()
    }

    private fun startGame() {
        gameViewModel.getGame(game.id)
        gameViewModel.game.observe(viewLifecycleOwner) {
            if (game.id == it.id) {
                game = it

                when (game.status) {
                    GameStatus.STARTED ->
                        when (game.turn) {
                            currentPlayer.id -> {
                                updateStatus("It's your turn!")
                                enableButtons()
                            }
                            otherPlayer.id -> {
                                updateStatus("It's ${otherPlayer.name}'s turn!")
                                disableButtons()
                            }
                            else -> Unit
                        }
                    GameStatus.FINISHED -> endGame()
                    GameStatus.PENDING -> navigateSafe(
                        R.id.action_game_to_lobby,
                        bundleOf("title" to game.name, LobbyFragment.GAME_KEY to game)
                    )
                }

                updateBoard()
            }
        }
    }

    private fun endGame() {
        disableButtons()

        checkWinner(game.winner) {
            object : CountDownTimer(10000, 1000) {
                override fun onTick(secondsUntilDone: Long) {
                    val seconds = (secondsUntilDone / 1000).toDouble().roundToInt()
                    updateStatus(
                        when (game.winner) {
                            currentPlayer.id -> "You won! Ending game in $seconds "
                            otherPlayer.id -> "You lost... Ending game in $seconds"
                            else -> "Someone won the game...? Ending game in $seconds"
                        }
                    )
                }

                override fun onFinish() = navigateSafe(R.id.action_game_to_game_overview)
            }.start()
        }
    }

    private fun updateBoard() {
        rows.forEachIndexed { rowIndex, view ->
            (0..6).forEach { cellIndex ->
                val cellValue = game.board["row $rowIndex"]?.get("cell $cellIndex")

                getCellView(cellIndex, view)?.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        when (cellValue) {
                            currentPlayer.id -> R.color.current_player
                            otherPlayer.id -> R.color.other_player
                            else -> R.color.no_player
                        }
                    )
                )
            }
        }
    }

    private fun updateStatus(status: String) {
        match_status?.text = status
    }

    private fun disableButtons() {
        buttons.forEach {
            it as ImageView
            it.setOnClickListener { }
            it.setImageResource(R.drawable.ic_triangle_not_active)
        }
    }

    private fun enableButtons() {
        buttons.forEachIndexed { index, button ->
            button as ImageView
            button.setOnClickListener { insertCoin(index) }
            button.setImageResource(R.drawable.ic_triangle_active)
        }
    }

    private fun insertCoin(cellIndex: Int) {
        rows.forEachIndexed { rowIndex, view ->
            val cell = getCellView(cellIndex, view)
            val cellBackground = cell?.cardBackgroundColor?.defaultColor

            if (rows.size > rowIndex + 1) {
                val nextCell = getCellView(cellIndex, rows[rowIndex + 1])
                val nextCellBackground = nextCell?.cardBackgroundColor?.defaultColor

                if (nextCellBackground != -1 && cellBackground == -1) setCoin(rowIndex, cellIndex)
            } else if (cellBackground == -1) setCoin(rowIndex, cellIndex)
        }
    }

    private fun setCoin(rowIndex: Int, cellIndex: Int) {
        if (game.board["row $rowIndex"] == null)
            game.board["row $rowIndex"] = hashMapOf(Pair("cell $cellIndex", currentPlayer.id))
        else game.board["row $rowIndex"]!!["cell $cellIndex"] = currentPlayer.id

        checkWinner(currentPlayer.id) {
            if (it) {
                game.turn = ""
                game.winner = currentPlayer.id
                game.status = GameStatus.FINISHED
            } else game.turn = otherPlayer.id

            gameViewModel.updateGame(game)
        }
    }

    private fun checkWinner(playerId: String, callback: (Boolean) -> Unit) {
        callback(
            checkHorizontal(playerId) ||
                    checkVertical(playerId) ||
                    checkDiagonalBackwards(playerId) ||
                    checkDiagonalForward(playerId)
        )
    }

    private fun checkVertical(playerId: String): Boolean {
        (0..6).forEach { cell ->
            val winningCells = arrayListOf<MaterialCardView>()

            rows.forEachIndexed { row, view ->
                val cellValue = game.board["row $row"]?.get("cell $cell")
                val cellView = getCellView(cell, view)

                if (cellValue == playerId && cellView != null) {
                    winningCells.add(cellView)

                    if (winningCells.size == 4) {
                        setWinner(winningCells)
                        return true
                    }
                } else winningCells.clear()
            }
        }
        return false
    }

    private fun checkHorizontal(playerId: String): Boolean {
        rows.forEachIndexed { row, view ->
            val winningCells = arrayListOf<MaterialCardView>()

            (0..6).forEach { cell ->
                val cellValue = game.board["row $row"]?.get("cell $cell")
                val cellView = getCellView(cell, view)

                if (cellValue == playerId && cellView != null) {
                    winningCells.add(cellView)

                    if (winningCells.size == 4) {
                        setWinner(winningCells)
                        return true
                    }
                } else winningCells.clear()
            }
        }
        return false
    }

    private fun checkDiagonalBackwards(playerId: String): Boolean {
        rows.forEachIndexed { row, view ->
            (0..6).forEach { cellIndex ->
                val currentCellValue = game.board["row $row"]?.get("cell $cellIndex")
                val cellValueTwo = game.board["row ${row + 1}"]?.get("cell ${cellIndex + 1}")
                val cellValueThree = game.board["row ${row + 2}"]?.get("cell ${cellIndex + 2}")
                val cellValueFour = game.board["row ${row + 3}"]?.get("cell ${cellIndex + 3}")

                if (currentCellValue == playerId && cellValueTwo == playerId && cellValueThree == playerId && cellValueFour == playerId) {
                    setWinner(
                        arrayListOf(
                            getCellView(cellIndex, view)!!,
                            getCellView(cellIndex + 1, rows[row + 1])!!,
                            getCellView(cellIndex + 2, rows[row + 2])!!,
                            getCellView(cellIndex + 3, rows[row + 3])!!
                        )
                    )
                    return true
                }
            }
        }

        return false
    }

    private fun checkDiagonalForward(playerId: String): Boolean {
        rows.forEachIndexed { rowIndex, view ->
            (0..6).forEach { cellIndex ->
                val currentCellValue = game.board["row $rowIndex"]?.get("cell $cellIndex")
                val cellValueTwo = game.board["row ${rowIndex - 1}"]?.get("cell ${cellIndex + 1}")
                val cellValueThree = game.board["row ${rowIndex - 2}"]?.get("cell ${cellIndex + 2}")
                val cellValueFour = game.board["row ${rowIndex - 3}"]?.get("cell ${cellIndex + 3}")

                if (currentCellValue == playerId && cellValueTwo == playerId && cellValueThree == playerId && cellValueFour == playerId) {
                    setWinner(
                        arrayListOf(
                            getCellView(cellIndex, view)!!,
                            getCellView(cellIndex + 1, rows[rowIndex - 1])!!,
                            getCellView(cellIndex + 2, rows[rowIndex - 2])!!,
                            getCellView(cellIndex + 3, rows[rowIndex - 3])!!
                        )
                    )
                    return true
                }
            }
        }
        return false
    }

    private fun setWinner(cells: ArrayList<MaterialCardView>) = cells.forEach {
        it.strokeWidth = 15
        it.strokeColor = ContextCompat.getColor(requireContext(), R.color.winner)
    }

    private fun getCellView(index: Int, row: View) = when (index) {
        0 -> row.cell1
        1 -> row.cell2
        2 -> row.cell3
        3 -> row.cell4
        4 -> row.cell5
        5 -> row.cell6
        6 -> row.cell7
        else -> null
    }
}