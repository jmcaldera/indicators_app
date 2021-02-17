package dev.josecaldera.indicators.main.ui.holder

import androidx.recyclerview.widget.RecyclerView
import dev.josecaldera.indicators.R
import dev.josecaldera.indicators.databinding.LayoutItemIndicatorBinding
import dev.josecaldera.indicators.main.domain.model.Indicator
import dev.josecaldera.indicators.main.ui.IndicatorItem

class IndicatorViewHolder(
    private val binding: LayoutItemIndicatorBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: IndicatorItem) {
        binding.root.setOnClickListener {
            item.onClick.invoke(item.indicator)
        }
        binding.name.text = item.indicator.name
        binding.value.text = binding.root.context.getString(
            R.string.indicator_value,
            item.indicator.value,
            item.indicator.unit.asUnit()
        )
    }
}

private fun Indicator.UnitType.asUnit(): String {
    return when (this) {
        Indicator.UnitType.PESOS -> "CLP"
        Indicator.UnitType.PERCENTAGE -> "%"
        Indicator.UnitType.DOLLAR -> "USD"
        Indicator.UnitType.UNKNOWN -> ""
    }
}
