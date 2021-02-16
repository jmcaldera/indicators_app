package dev.josecaldera.indicators.main.ui.holder

import androidx.recyclerview.widget.RecyclerView
import dev.josecaldera.indicators.databinding.LayoutItemIndicatorBinding
import dev.josecaldera.indicators.main.ui.IndicatorItem

class IndicatorViewHolder(
    private val binding: LayoutItemIndicatorBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: IndicatorItem) {
        binding.root.setOnClickListener {
            item.onClick.invoke(item.indicator)
        }
        binding.name.text = item.indicator.name
        binding.value.text = item.indicator.value
    }
}
