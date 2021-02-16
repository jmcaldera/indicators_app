package dev.josecaldera.indicators.main.data.api

import dev.josecaldera.indicators.main.domain.model.Indicator

internal fun NetworkIndicator.toDomain(): Indicator {
    return Indicator(
        code = code,
        name = name,
        date = date,
        value = value,
        unit = mapUnitType(unit)
    )
}

private fun mapUnitType(unit: String): Indicator.UnitType {
    return when {
        unit.equals("Dólar", true) -> Indicator.UnitType.DOLLAR
        unit.equals("Porcentaje", true) -> Indicator.UnitType.PERCENTAGE
        unit.equals("Pesos", true) -> Indicator.UnitType.PESOS
        else -> Indicator.UnitType.UNKNOWN
    }
}
