package dev.josecaldera.indicators.main.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.josecaldera.indicators.R
import dev.josecaldera.indicators.databinding.FragmentIndicatorsBinding
import dev.josecaldera.indicators.main.ui.adapter.IndicatorsAdapter
import dev.josecaldera.indicators.toolbar.ToolbarViewModel
import dev.josecaldera.indicators.utils.hideSoftKeyboard
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class IndicatorsFragment : Fragment() {

    private val toolbarViewModel: ToolbarViewModel by activityViewModels()
    private val viewModel: IndicatorsViewModel by viewModels()
    private lateinit var binding: FragmentIndicatorsBinding
    private lateinit var indicatorsAdapter: IndicatorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIndicatorsBinding
            .inflate(inflater, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
        observeEvents()

        if (savedInstanceState == null) {
            viewModel.fetchIndicators()
        }
    }

    private fun setupViews() {
        indicatorsAdapter = IndicatorsAdapter()

        binding.listIndicators.apply {
            adapter = indicatorsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

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
                    IndicatorsViewModel.Event.HideSoftKeyboard -> hideSoftKeyboard()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_indicators, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_search)

        val searchManager = requireActivity()
            .getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (item != null) {
            (item.actionView as? SearchView)?.let { actionView ->

                viewModel.savedQuery.observe(viewLifecycleOwner, { saved ->

                    // Restore previously saved query on view recreation
                    val query = actionView.query.toString()
                    if (!saved.isNullOrBlank() && query != saved) {
                        actionView.setQuery(saved, false)

                        // Expand searchView
                        actionView.isIconified = false
                    }
                })

                actionView.setSearchableInfo(
                    searchManager.getSearchableInfo(requireActivity().componentName)
                )

                actionView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        viewModel.onQuerySubmitted(query)
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        viewModel.onQueryChanged(newText)
                        return true
                    }
                })
            }
        }
    }
}
