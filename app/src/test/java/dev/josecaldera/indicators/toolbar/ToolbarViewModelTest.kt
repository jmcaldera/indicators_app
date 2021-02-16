package dev.josecaldera.indicators.toolbar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


class ToolbarViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: ToolbarViewModel

    @Before
    fun setUp() {
        viewModel = ToolbarViewModel()
    }

    @Test
    fun `GIVEN viewModel WHEN init THEN is visible`() {
        assertTrue(viewModel.isVisible.value!!)
    }

    @Test
    fun `GIVEN viewModel WHEN init THEN title is null`() {
        assertNull(viewModel.title.value)
    }

    @Test
    fun `GIVEN viewModel WHEN show THEN is visible`() {
        viewModel.show()
        assertTrue(viewModel.isVisible.value!!)
    }

    @Test
    fun `GIVEN viewModel WHEN hide THEN is not visible`() {
        viewModel.hide()
        assertFalse(viewModel.isVisible.value!!)
    }

    @Test
    fun `GIVEN viewModel WHEN setTitle THEN title is updated`() {
        viewModel.setTitle("title")
        assertEquals("title", viewModel.title.value!!)
    }
}
