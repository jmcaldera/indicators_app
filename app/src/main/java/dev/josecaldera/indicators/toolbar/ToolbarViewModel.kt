package dev.josecaldera.indicators.toolbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToolbarViewModel : ViewModel() {

    private val visibility = MutableLiveData(true)
    val isVisible: LiveData<Boolean> = visibility

    private val customTitle = MutableLiveData<String>(null)
    val title: LiveData<String> = customTitle

    fun show() {
        visibility.value = true
    }

    fun hide() {
        visibility.value = false
    }

    fun setTitle(title: String) {
        customTitle.value = title
    }
}
