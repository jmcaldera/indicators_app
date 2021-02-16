package dev.josecaldera.indicators.main.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.josecaldera.indicators.databinding.LayoutItemIndicatorBinding
import dev.josecaldera.indicators.main.domain.model.Indicator
import dev.josecaldera.indicators.main.ui.holder.IndicatorViewHolder

class IndicatorsAdapter(
    private val onClick: (Indicator) -> Unit
) : ListAdapter<Indicator, IndicatorViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
        val binding = LayoutItemIndicatorBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return IndicatorViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Indicator>() {
    override fun areItemsTheSame(oldItem: Indicator, newItem: Indicator): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Indicator, newItem: Indicator): Boolean {
        return oldItem == newItem
    }
}

interface RecyclerViewItem