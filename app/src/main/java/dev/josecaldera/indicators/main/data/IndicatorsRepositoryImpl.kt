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

    override suspend fun getIndicators(): Result<List<Indicator>> {
        return withContext(ioDispatcher) {
            try {
                api.getIndicators()
                    .map { it.toDomain() }
                    .let { Result.OnSuccess(it) }
            } catch (t: Throwable) {
                Result.OnError(IndicatorsError(t.localizedMessage.orEmpty(), t))
            }
        }
    }
}
