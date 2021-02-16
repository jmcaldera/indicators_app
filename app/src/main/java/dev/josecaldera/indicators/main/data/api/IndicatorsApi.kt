package dev.josecaldera.indicators.main.data.api

import retrofit2.http.GET

interface IndicatorsApi {

    @GET("/api")
    suspend fun getIndicators(): List<NetworkIndicator>
}
