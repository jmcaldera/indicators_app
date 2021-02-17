package dev.josecaldera.indicators.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.josecaldera.indicators.databinding.FragmentLandingBinding
import dev.josecaldera.indicators.landing.LandingViewModel.Event
import dev.josecaldera.indicators.toolbar.ToolbarViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LandingFragment : Fragment() {

    private val toolbarViewModel: ToolbarViewModel by activityViewModels()
    private val viewModel: LandingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLandingBinding
            .inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEvents()
        hideToolbar()
    }

    private fun hideToolbar() {
        toolbarViewModel.hide()
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            // show landing for 1 sec
            delay(1000)
            viewModel.checkUserStatus()
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is Event.NavigateToIndicators -> findNavController()
                        .navigate(LandingFragmentDirections.actionLandingToIndicators(event.name))
                    Event.NavigateToLogin -> findNavController()
                        .navigate(LandingFragmentDirections.actionLandingToLogin())
                }
            }
        }
    }
}
