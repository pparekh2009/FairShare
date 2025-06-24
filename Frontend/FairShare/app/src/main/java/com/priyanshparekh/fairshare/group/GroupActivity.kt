package com.priyanshparekh.fairshare.group

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.ActivityGroupBinding
import com.priyanshparekh.fairshare.utils.Status

class GroupActivity : AppCompatActivity() {

    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var binding: ActivityGroupBinding
    private lateinit var navController: NavController

    private lateinit var groupViewModel: GroupViewModel

    var groupId: Long = -1L
    var groupName: String = ""
    lateinit var usernames: HashMap<Long, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolBar)

        groupViewModel = ViewModelProvider(this)[GroupViewModel::class.java]

        groupId = intent.getLongExtra("groupId", -1)
        groupName = intent.getStringExtra("groupName") ?: ""

        groupViewModel.getGroupMembers(groupId)

        usernames = HashMap()

        groupViewModel.groupMembersStatus.observe(this) { status ->
            when (status) {
                is Status.ERROR -> Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                is Status.LOADING -> { }
                is Status.SUCCESS -> {
                    binding.navView.getHeaderView(0).findViewById<TextView>(R.id.no_of_members_label).text = "${status.data.size} Members"

                    for (user in status.data) {
                        usernames[user.id!!] = user.name
                    }
                }
            }
        }

        navController = supportFragmentManager.findFragmentById(R.id.fragment_container)?.findNavController()!!

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // Handle drawer menu clicks via navController
        binding.navView.setupWithNavController(navController)

        // Sync drawer icon in toolbar
        appBarConfig = AppBarConfiguration(
            navController.graph,
            binding.drawerLayout
        )

        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.group_name_label).text = groupName
        binding.toolBar.title = groupName

        setupActionBarWithNavController(navController, appBarConfig)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.nav_add_members || destination.id == R.id.nav_payment) {
                binding.toolBar.visibility = View.GONE
            } else {
                binding.toolBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }
}