package dev.josecaldera.indicators.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import dev.josecaldera.indicators.args.toParcelable
import dev.josecaldera.indicators.main.domain.model.Indicator
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class IndicatorDetailsViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Test
    fun `GIVEN arg WHEN init THEN update indicator`() {
        val arg = fakeIndicator().toParcelable()

        createViewModel(SavedStateHandle().apply {
            set(IndicatorDetailsViewModel.ARG_INDICATOR, arg)
        }).whileObserving {
            assertEquals(arg.toDetailsModel(), indicator.value)
        }
    }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ): IndicatorDetailsViewModel {
        return IndicatorDetailsViewModel(savedStateHandle)
    }

    private fun IndicatorDetailsViewModel.whileObserving(block: IndicatorDetailsViewModel.() -> Unit) {
        val itemObserver: Observer<IndicatorDetailsModel> = Observer { }

        indicator.observeForever(itemObserver)

        block(this)

        indicator.removeObserver(itemObserver)
    }

    private fun fakeIndicator(): Indicator {
        return Indicator(
            code = "code",
            name = "name",
            unit = Indicator.UnitType.DOLLAR,
            date = "2021-02-16T18:00:00.000Z",
            value = "123"
        )
    }
}
