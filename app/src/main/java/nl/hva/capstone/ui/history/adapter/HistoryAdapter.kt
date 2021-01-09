package nl.hva.capstone.ui.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_history_game.view.*
import nl.hva.capstone.MainActivity
import nl.hva.capstone.R
import nl.hva.capstone.models.Game

class HistoryAdapter(private val games: List<Game>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_history_game, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.init(games[position])

    override fun getItemCount() = games.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun init(game: Game) {
            val context = itemView.context
            val activity = context as MainActivity

            itemView.txt_versus.text = context.getString(
                R.string.game_versus,
                game.players.first { it.id != activity.currentPlayer?.id }.name
            )

            itemView.txt_result.text = context.getString(
                if (game.winner == auth.currentUser?.uid) R.string.game_result_won
                else R.string.game_result_lost
            )
        }
    }
}