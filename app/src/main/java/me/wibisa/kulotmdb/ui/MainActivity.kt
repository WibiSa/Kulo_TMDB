package me.wibisa.kulotmdb.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import me.wibisa.kulotmdb.R
import me.wibisa.kulotmdb.core.util.gone
import me.wibisa.kulotmdb.core.util.visible
import me.wibisa.kulotmdb.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val mainNavController = this.findNavController(R.id.main_nav_host)
        binding.bottomNav.setupWithNavController(mainNavController)
        mainNavController.addOnDestinationChangedListener { _, destination, _ ->
            val isDestinationValid =
                destination.id == R.id.discoverFragment || destination.id == R.id.genreFragment
            if (isDestinationValid)
                binding.bottomNav.visible()
            else
                binding.bottomNav.gone()
        }
    }
}