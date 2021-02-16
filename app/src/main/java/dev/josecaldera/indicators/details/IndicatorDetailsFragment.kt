package dev.josecaldera.indicators.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dev.josecaldera.indicators.databinding.FragmentIndicatorDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class IndicatorDetailsFragment : Fragment() {

    private val args: IndicatorDetailsFragmentArgs by navArgs()

    private val viewModel: IndicatorDetailsViewModel by viewModel(
        state = { bundleOf(IndicatorDetailsViewModel.ARG_INDICATOR to args.indicator) }
    )
    private lateinit var binding: FragmentIndicatorDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIndicatorDetailsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        viewModel.indicator.observe(viewLifecycleOwner, { indicator ->
            with(indicator) {
                binding.name.text = name
                binding.code.text = code
                binding.date.text = date
                binding.unit.text = unit
                binding.value.text = value
            }
        })
    }
}
