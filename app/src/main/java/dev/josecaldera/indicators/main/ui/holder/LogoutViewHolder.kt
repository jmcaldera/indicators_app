package dev.josecaldera.indicators.main.ui.holder

import androidx.recyclerview.widget.RecyclerView
import dev.josecaldera.indicators.databinding.LayoutItemLogoutButtonBinding
import dev.josecaldera.indicators.main.ui.LogoutItem

class LogoutViewHolder(
    private val binding: LayoutItemLogoutButtonBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: LogoutItem) {
        binding.buttonLogOut.setOnClickListener {
            item.onClick.invoke()
        }
    }
}
