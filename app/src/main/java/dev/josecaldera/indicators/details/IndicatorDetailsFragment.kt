package dev.josecaldera.indicators.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.josecaldera.indicators.databinding.FragmentIndicatorDetailsBinding

@AndroidEntryPoint
class IndicatorDetailsFragment : Fragment() {

    private val viewModel: IndicatorDetailsViewModel by viewModels()
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
