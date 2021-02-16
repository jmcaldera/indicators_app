package dev.josecaldera.indicators.main.domain.model

import dev.josecaldera.indicators.core.Error

class IndicatorsError(
    message: String,
    throwable: Throwable? = null
) : Error(message, throwable)
