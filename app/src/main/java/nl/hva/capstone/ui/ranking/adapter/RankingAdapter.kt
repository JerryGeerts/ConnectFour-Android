package nl.hva.capstone.ui.ranking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_ranking.view.*
import nl.hva.capstone.R
import nl.hva.capstone.models.PlayerScore

class RankingAdapter(private val playerScores: List<PlayerScore>) :
    RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_ranking, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.init(playerScores[position])

    override fun getItemCount() = playerScores.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun init(playerScore: PlayerScore) {
            itemView.txt_player_name.text = playerScore.name
            itemView.tv_win_rate.text = itemView.resources.getString(
                R.string.player_score_win_rate,
                playerScore.win,
                playerScore.lose
            )
        }
    }
}