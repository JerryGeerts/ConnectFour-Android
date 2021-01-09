package nl.hva.capstone

import android.os.Bundle
import android.view.Menu
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.nav_header_main.*
import nl.hva.capstone.models.Player
import nl.hva.capstone.viewModels.PlayerViewModel


class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = Firebase.auth
    private val playerViewModel: PlayerViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration

    var currentPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        initNavigation()
        getPlayer()
    }

    private fun getPlayer() {
        val uid = auth.currentUser?.uid

        if (uid == null) auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) getPlayer()
                else println(task.exception?.message)
            }
        else {
            playerViewModel.getPlayer(uid)
            playerViewModel.player.observe(this) {
                if (it?.name == null) showCreateUserDialog(uid)
                else setPlayer(it)
            }
        }
    }

    private fun showCreateUserDialog(uid: String) {
        val view = layoutInflater.inflate(R.layout.dialog_add, null)
        val textBox = view.findViewById<EditText>(R.id.et_name)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setTitle("Create player")
            .setCancelable(false)
            .setPositiveButton("Create") { _, _ ->
                val newPlayer = Player(uid, textBox.text.toString())
                playerViewModel.addPlayer(newPlayer)
                setPlayer(newPlayer)
            }.create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        textBox.addTextChangedListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it?.isNotEmpty() ?: false
        }
    }

    private fun setPlayer(player: Player) {
        menu_user_name.text = getString(R.string.menu_user_name, player.name)
        currentPlayer = player
    }

    private fun initNavigation() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_game_overview,
                R.id.nav_history,
                R.id.nav_ranking,
                R.id.nav_active_games
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)

        return if (navController.currentDestination?.id == R.id.nav_game) {
            navController.navigate(R.id.action_game_to_active_games)
            true
        } else navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}