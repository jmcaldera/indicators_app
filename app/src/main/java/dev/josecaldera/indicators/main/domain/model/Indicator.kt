package dev.josecaldera.indicators.main.domain.model

data class Indicator(
    val code: String,
    val name: String,
    val unit: UnitType,
    val date: String,
    val value: String
) {

    enum class UnitType {
        PESOS,
        PERCENTAGE,
        DOLLAR,
        UNKNOWN
    }
}