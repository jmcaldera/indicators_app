package dev.josecaldera.indicators

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.josecaldera.indicators.databinding.ActivityMainBinding
import dev.josecaldera.indicators.toolbar.ToolbarViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val toolbarViewModel: ToolbarViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        observeToolbarState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        val appBarConfig = AppBarConfiguration.Builder(
            R.id.destination_indicators,
            R.id.destination_login
        ).build()

        val navController = findNavController(R.id.nav_host_container)
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun observeToolbarState() {
        toolbarViewModel.isVisible.observe(this, { visible ->
            supportActionBar?.let {
                if (visible) it.show()
                else it.hide()
            }
        })

        toolbarViewModel.title.observe(this, { title ->
            supportActionBar?.let {
                if (!title.isNullOrBlank()) {
                    it.title = title
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_container).navigateUp()
    }
}
