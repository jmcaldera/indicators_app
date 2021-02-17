package dev.josecaldera.indicators.main.data

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.main.data.api.IndicatorsApi
import dev.josecaldera.indicators.main.data.api.toDomain
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import dev.josecaldera.indicators.main.domain.model.Indicator
import dev.josecaldera.indicators.main.domain.model.IndicatorsError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IndicatorsRepositoryImpl(
    private val api: IndicatorsApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IndicatorsRepository {

    private var cachedIndicators = mutableListOf<Indicator>()

    override suspend fun getIndicators(): Result<List<Indicator>> {
        return withContext(ioDispatcher) {
            try {
                api.getIndicators()
                    .map { it.toDomain() }
                    .let {
                        cacheIndicators(it)
                        Result.OnSuccess(it)
                    }
            } catch (t: Throwable) {
                Result.OnError(IndicatorsError(t.localizedMessage.orEmpty(), t))
            }
        }
    }

    override suspend fun getIndicatorsForCode(code: String): Result<List<Indicator>> {
        if (code.isBlank()) return Result.OnSuccess(cachedIndicators)
        val indicators = cachedIndicators.filter { it.code.contains(code) }
        return indicators.let {
            Result.OnSuccess(it)
        }
    }

    private fun cacheIndicators(indicators: List<Indicator>) {
        cachedIndicators.clear()
        cachedIndicators.addAll(indicators)
    }
}
