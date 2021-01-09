package nl.hva.capstone.ui.gameOverview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_available_game.view.*
import nl.hva.capstone.R
import nl.hva.capstone.models.Game

class GameOverviewAdapter(
    private val games: List<Game>,
    private val joinGame: (Game) -> Unit

) :
    RecyclerView.Adapter<GameOverviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_available_game, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.init(games[position])
    }

    override fun getItemCount(): Int {
        return games.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun init(game: Game) {
            itemView.setOnClickListener { joinGame(game) }
            itemView.txt_game_name.text = game.name
            itemView.txt_result.text =
                itemView.resources.getString(R.string.player_count, game.players.size.toString())
        }
    }
}