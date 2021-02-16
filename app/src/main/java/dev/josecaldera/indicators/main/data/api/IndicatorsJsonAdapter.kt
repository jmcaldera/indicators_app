package dev.josecaldera.indicators.main.data.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class IndicatorsJsonAdapter {

    @FromJson
    fun indicatorsFromJson(response: IndicatorsResponse): List<NetworkIndicator> {
        return with(response) {
            listOf(
                uf, ivp, dollar, dollarDeal, euro, ipc, utm, imacec,
                tpm, copperPound, unemploymentRate, bitcoin
            )
        }
    }

    @ToJson
    fun indicatorsToJson(indicators: List<NetworkIndicator>): IndicatorsResponse {
        return IndicatorsResponse(
            uf = indicators.first { it.code == "uf" },
            ivp = indicators.first { it.code == "ivp" },
            dollar= indicators.first { it.code == "dolar" },
            dollarDeal = indicators.first { it.code == "dolar_intercambio" },
            euro = indicators.first { it.code == "euro" },
            ipc = indicators.first { it.code == "ipc" },
            utm = indicators.first { it.code == "utm" },
            imacec = indicators.first { it.code == "imacec" },
            tpm = indicators.first { it.code == "tpm" },
            copperPound = indicators.first { it.code == "libra_cobre" },
            unemploymentRate = indicators.first { it.code == "tasa_desempleo" },
            bitcoin = indicators.first { it.code == "bitcoin" }
        )
    }
}
