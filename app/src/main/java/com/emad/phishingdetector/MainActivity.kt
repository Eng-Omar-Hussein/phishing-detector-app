package com.emad.phishingdetector

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.emad.data.local.SessionManager
import com.emad.phishingdetector.databinding.ActivityMainBinding
import com.emad.phishingdetector.presentation.auth.WelcomeActivity
import com.emad.phishingdetector.presentation.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint // Critical: Tells Hilt to inject dependencies into this Activity
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // Inject SessionManager to get the user's details for the header
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Setup Toolbar
        // Tell Android to use our custom Toolbar instead of the default Action Bar
        setSupportActionBar(binding.toolbar)

        // 3. Find the NavController
        // We get it from the FragmentContainerView in activity_main.xml
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 4. Setup AppBarConfiguration
        // This tells the Toolbar which screens are "Top Level" (showing the Hamburger icon)
        // If a screen isn't in this list (like EmailDetail), it will show a Back Arrow.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home // Add other menu IDs here later like R.id.nav_sent, R.id.nav_spam
            ),
            binding.drawerLayout // Ties the Hamburger icon to the Drawer
        )

        // 5. Connect Everything Together
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inbox -> navigateToHomeWithFolder("INBOX")
                R.id.nav_sent -> navigateToHomeWithFolder("SENT")
                R.id.nav_spam -> navigateToHomeWithFolder("SPAM")
                R.id.nav_trash -> navigateToHomeWithFolder("TRASH")
                R.id.nav_hooked -> navigateToHomeWithFolder("HOOKED")
                // Starred and others we’ll handle separately
                R.id.nav_starred -> navigateToHomeWithFolder("STARRED") // we’ll map this specially
                R.id.nav_logout -> handleLogout()
                else -> {
                    // For now, let NavigationUI handle non-folder items
                    val handled = androidx.navigation.ui.NavigationUI
                        .onNavDestinationSelected(menuItem, navController)
                    if (!handled) {
                        // future: Settings/Help screens
                    }
                }
            }

            binding.drawerLayout.closeDrawers()
            true
        }

        // 6. Populate the Drawer Header
        setupDrawerHeader()
    }

    private fun setupDrawerHeader() {
        // Get the header view from the NavigationView (index 0)
        val headerView = binding.navView.getHeaderView(0)

        // Find the TextViews inside the header
        val tvName = headerView.findViewById<TextView>(R.id.header_name)
        val tvEmail = headerView.findViewById<TextView>(R.id.header_email)
        // val imgProfile = headerView.findViewById<ImageView>(R.id.imageView)

        // Set the text from SharedPreferences (SessionManager)
        tvName.text = sessionManager.getUserName() ?: "Unknown User"
        tvEmail.text = sessionManager.getUserEmail() ?: ""

        // Note: To load the profile picture URL into the ImageView,
        // you will need an image loading library like Glide or Coil later.
    }

    // 7. Handle the Up/Hamburger button clicks
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun navigateToHomeWithFolder(folderKey: String) {
        // Ensure we’re on HomeFragment first
        if (navController.currentDestination?.id != R.id.nav_home) {
            navController.navigate(R.id.nav_home)
        }

        // Pass folder selection down via Fragment result API or arguments.
        // Easiest: use FragmentResult.
        supportFragmentManager.setFragmentResult(
            HomeFragment.REQUEST_FOLDER_CHANGE,
            Bundle().apply { putString(HomeFragment.KEY_FOLDER, folderKey) }
        )
    }

    private fun handleLogout() {
        sessionManager.clearSession()
        // Go to WelcomeActivity
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

