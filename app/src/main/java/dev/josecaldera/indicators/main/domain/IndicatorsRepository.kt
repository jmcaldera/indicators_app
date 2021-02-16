package dev.josecaldera.indicators.main.domain

import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.main.domain.model.Indicator

interface IndicatorsRepository {

    suspend fun getIndicators(): Result<List<Indicator>>
}
