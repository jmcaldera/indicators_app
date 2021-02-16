package dev.josecaldera.indicators.main.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IndicatorsResponse(
    val uf: NetworkIndicator,
    val ivp: NetworkIndicator,
    @Json(name = "dolar") val dollar: NetworkIndicator,
    @Json(name = "dolar_intercambio") val dollarDeal: NetworkIndicator,
    val euro: NetworkIndicator,
    val ipc: NetworkIndicator,
    val utm: NetworkIndicator,
    val imacec: NetworkIndicator,
    val tpm: NetworkIndicator,
    @Json(name = "libra_cobre") val copperPound: NetworkIndicator,
    @Json(name = "tasa_desempleo") val unemploymentRate: NetworkIndicator,
    val bitcoin: NetworkIndicator
)

@JsonClass(generateAdapter = true)
data class NetworkIndicator(
    @Json(name = "codigo") val code: String,
    @Json(name = "nombre") val name: String,
    @Json(name = "unidad_medida") val unit: String,
    @Json(name = "fecha") val date: String,
    @Json(name = "valor") val value: String
)
