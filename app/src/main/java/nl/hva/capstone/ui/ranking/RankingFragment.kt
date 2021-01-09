package nl.hva.capstone.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_rankings.*
import nl.hva.capstone.R
import nl.hva.capstone.models.GameStatus
import nl.hva.capstone.models.PlayerScore
import nl.hva.capstone.ui.ranking.adapter.RankingAdapter
import nl.hva.capstone.ui.ranking.viewModel.RankingViewModel

class RankingFragment : Fragment() {

    private val rankingViewModel: RankingViewModel by activityViewModels()

    private lateinit var rankingAdapter: RankingAdapter

    private val playerScores = arrayListOf<PlayerScore>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_rankings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecyclerView() {
        rankingAdapter = RankingAdapter(playerScores)
        rv_ranking.adapter = rankingAdapter
        rv_ranking.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        getGames()
    }

    private fun getGames() {
        rankingViewModel.getGames()
        rankingViewModel.games.observe(viewLifecycleOwner) { allGames ->
            val scores = arrayListOf<PlayerScore>()

            allGames?.forEach { game ->
                if (game.status == GameStatus.FINISHED) {
                    game.players.forEach { player ->
                        val playerScore = scores.firstOrNull {
                            it.id == player.id
                        } ?: PlayerScore(player.id, player.name)

                        if (scores.contains(playerScore)) scores.remove(playerScore)
                        if (game.winner == player.id) playerScore.win++
                        else playerScore.lose++

                        scores.add(playerScore)
                    }
                }
            }

            playerScores.clear()
            playerScores.addAll(scores.sortedByDescending { it.win })
            rankingAdapter.notifyDataSetChanged()
        }
    }
}