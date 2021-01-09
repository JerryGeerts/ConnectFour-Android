package nl.hva.capstone.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_history.*
import nl.hva.capstone.R
import nl.hva.capstone.models.Game
import nl.hva.capstone.models.GameStatus
import nl.hva.capstone.ui.history.adapter.HistoryAdapter
import nl.hva.capstone.ui.history.viewModel.HistoryViewModel

class HistoryFragment : Fragment() {

    private val auth: FirebaseAuth = Firebase.auth
    private val historyViewModel: HistoryViewModel by activityViewModels()

    private lateinit var historyAdapter: HistoryAdapter

    private val games = arrayListOf<Game>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecyclerView() {
        historyAdapter = HistoryAdapter(games)
        rv_history.adapter = historyAdapter
        rv_history.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        getGames()
    }

    private fun getGames() {
        historyViewModel.getGames()
        historyViewModel.games.observe(viewLifecycleOwner) { allGames ->
            games.clear()

            allGames?.filter {
                it.status == GameStatus.FINISHED && it.players.firstOrNull { player -> player.id == auth.currentUser?.uid } != null
            }?.toList()?.let { games.addAll(it) }

            historyAdapter.notifyDataSetChanged()
        }
    }
}