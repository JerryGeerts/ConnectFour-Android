package nl.hva.capstone.ui.lobby.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_player.view.*
import nl.hva.capstone.R
import nl.hva.capstone.models.GamePlayer

class LobbyAdapter(private val players: List<GamePlayer>) :
    RecyclerView.Adapter<LobbyAdapter.ViewHolder>() {

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.init(players[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun init(player: GamePlayer) {
            itemView.txt_player_name.text = player.name
            itemView.tv_winrate.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context, when {
                        player.owner -> R.drawable.ic_person
                        player.ready -> R.drawable.ic_check_mark
                        else -> android.R.color.transparent
                    }
                )
            )
        }
    }
}