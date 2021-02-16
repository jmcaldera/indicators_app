package dev.josecaldera.indicators.main.ui.holder

import androidx.recyclerview.widget.RecyclerView
import dev.josecaldera.indicators.databinding.LayoutItemIndicatorBinding
import dev.josecaldera.indicators.main.domain.model.Indicator

class IndicatorViewHolder(
    private val binding: LayoutItemIndicatorBinding,
    private val onClick: (Indicator) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(indicator: Indicator) {
        binding.root.setOnClickListener {
            onClick.invoke(indicator)
        }
        binding.name.text = indicator.name
        binding.value.text = indicator.value
    }
}
