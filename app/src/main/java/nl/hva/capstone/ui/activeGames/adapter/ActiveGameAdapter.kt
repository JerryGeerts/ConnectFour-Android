package nl.hva.capstone.ui.activeGames.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_active_game.view.*
import kotlinx.android.synthetic.main.item_available_game.view.txt_game_name
import nl.hva.capstone.MainActivity
import nl.hva.capstone.R
import nl.hva.capstone.models.Game

class ActiveGameAdapter(
    private val games: List<Game>,
    private val joinGame: (Game) -> Unit
) :
    RecyclerView.Adapter<ActiveGameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_active_game, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.init(games[position])

    override fun getItemCount() = games.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun init(game: Game) {
            val activity = (itemView.context as MainActivity)
            val playerTurn = game.players.firstOrNull { it.id == game.turn }
            val isCurrentPlayer = activity.currentPlayer?.name == playerTurn?.name

            itemView.txt_game_name.text = game.name
            itemView.txt_last_move.text = if (isCurrentPlayer) "You" else playerTurn?.name
            itemView.setOnClickListener { joinGame(game) }
        }
    }
}