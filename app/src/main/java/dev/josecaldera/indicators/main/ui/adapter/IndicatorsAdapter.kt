package dev.josecaldera.indicators.main.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.josecaldera.indicators.databinding.LayoutItemIndicatorBinding
import dev.josecaldera.indicators.databinding.LayoutItemLogoutButtonBinding
import dev.josecaldera.indicators.main.ui.IndicatorItem
import dev.josecaldera.indicators.main.ui.LogoutItem
import dev.josecaldera.indicators.main.ui.holder.IndicatorViewHolder
import dev.josecaldera.indicators.main.ui.holder.LogoutViewHolder


class IndicatorsAdapter() : ListAdapter<RecyclerViewItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val VIEW_INDICATOR = 1
        private const val VIEW_LOGOUT = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_INDICATOR -> {
                val binding = LayoutItemIndicatorBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                IndicatorViewHolder(binding)
            }
            VIEW_LOGOUT -> {
                val binding = LayoutItemLogoutButtonBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                LogoutViewHolder(binding)
            }
            else -> throw IllegalArgumentException("unknown viewType=$viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_INDICATOR -> {
                (holder as IndicatorViewHolder).bind(getItem(position) as IndicatorItem)
            }
            VIEW_LOGOUT -> {
                (holder as LogoutViewHolder).bind(getItem(position) as LogoutItem)
            }
        }
    }

    // Simple multi view type handling
    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            VIEW_LOGOUT
        } else VIEW_INDICATOR
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecyclerViewItem>() {
    override fun areItemsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem): Boolean {
        return oldItem == newItem
    }
}

interface RecyclerViewItem
