package dev.josecaldera.indicators.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dev.josecaldera.indicators.R
import dev.josecaldera.indicators.databinding.FragmentIndicatorsBinding
import dev.josecaldera.indicators.main.ui.adapter.IndicatorsAdapter
import dev.josecaldera.indicators.toolbar.ToolbarViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState

class IndicatorsFragment : Fragment() {

    private val toolbarViewModel: ToolbarViewModel by sharedViewModel()
    private val viewModel: IndicatorsViewModel by viewModel(state = emptyState())
    private lateinit var binding: FragmentIndicatorsBinding
    private lateinit var indicatorsAdapter: IndicatorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIndicatorsBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
        observeEvents()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchIndicators()
    }

    private fun setupViews() {
        indicatorsAdapter = IndicatorsAdapter()

        binding.listIndicators.apply {
            adapter = indicatorsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        toolbarViewModel.setTitle(getString(R.string.title_indicators, viewModel.userName))
        toolbarViewModel.show()
    }

    private fun observeState() {
        viewModel.isLoading.observe(viewLifecycleOwner, { loading ->
            binding.refreshIndicators.isRefreshing = loading
        })

        viewModel.items.observe(viewLifecycleOwner, { items ->
            indicatorsAdapter.submitList(items)
        })
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is IndicatorsViewModel.Event.Failure -> {
                        Snackbar.make(
                            requireView(),
                            R.string.error_message,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(R.string.button_retry) {
                            event.onRetry.invoke()
                        }.show()
                    }
                    IndicatorsViewModel.Event.Logout -> {
                        findNavController().navigate(
                            IndicatorsFragmentDirections.actionIndicatorsToLogin()
                        )
                    }
                    is IndicatorsViewModel.Event.NavigateToDetails -> {
                        findNavController().navigate(
                            IndicatorsFragmentDirections
                                .actionIndicatorsToIndicatorDetails(event.indicator)
                        )
                    }
                }
            }
        }
    }
}
