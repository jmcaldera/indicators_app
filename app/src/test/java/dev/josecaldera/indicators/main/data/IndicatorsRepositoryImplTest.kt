package dev.josecaldera.indicators.main.data

import dev.josecaldera.indicators.main.data.api.IndicatorsApi
import dev.josecaldera.indicators.main.data.api.NetworkIndicator
import dev.josecaldera.indicators.main.data.api.toDomain
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class IndicatorsRepositoryImplTest {

    private val api = mockk<IndicatorsApi>(relaxed = true)
    private lateinit var repository: IndicatorsRepository

    @Before
    fun setUp() {
        repository = IndicatorsRepositoryImpl(
            api = api,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun `GIVEN repository WHEN getIndicators THEN call api`() = runBlockingTest {
        repository.getIndicators()

        coVerify { api.getIndicators() }
    }

    @Test
    fun `GIVEN repository WHEN getIndicators success THEN return success`() = runBlockingTest {
        val indicator = NetworkIndicator(
            "code", "name", "unit", "date", "value"
        )

        coEvery { api.getIndicators() } returns listOf(indicator)

        val result = repository.getIndicators()

        assertTrue(result.isSuccess())
    }

    @Test
    fun `GIVEN repository WHEN getIndicators success THEN return list`() = runBlockingTest {
        val indicator = NetworkIndicator(
            "code", "name", "unit", "date", "value"
        )

        coEvery { api.getIndicators() } returns listOf(indicator)

        val result = repository.getIndicators().getOrNull()!!

        assertTrue(result.isNotEmpty())
        assertEquals(indicator.toDomain(), result.first())
    }
}
