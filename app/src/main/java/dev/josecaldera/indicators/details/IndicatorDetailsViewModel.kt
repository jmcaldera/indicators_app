package dev.josecaldera.indicators.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dev.josecaldera.indicators.args.IndicatorArg
import dev.josecaldera.indicators.main.domain.model.Indicator
import java.text.SimpleDateFormat
import java.util.*

class IndicatorDetailsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val ARG_INDICATOR = "ARG_INDICATOR"
    }

    private val _indicator = savedStateHandle.getLiveData<IndicatorArg>(ARG_INDICATOR)
    val indicator = _indicator.map { it.toDetailsModel() }
}

data class IndicatorDetailsModel(
    val code: String,
    val name: String,
    val unit: String,
    val date: String,
    val value: String
)

fun IndicatorArg.toDetailsModel(): IndicatorDetailsModel {

    fun Indicator.UnitType.toHumanReadable(): String {
        return when (this) {
            Indicator.UnitType.PESOS -> "Pesos"
            Indicator.UnitType.PERCENTAGE -> "Porcentaje"
            Indicator.UnitType.DOLLAR -> "Dólar"
            Indicator.UnitType.UNKNOWN -> "Desconocido"
        }
    }

    fun humanReadableDate(date: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMMM yyyy h:mm a", Locale.getDefault())
        return parser.parse(date)?.let { formatter.format(it) } ?: date
    }

    // FIXME a string wrapper or resources could be used here
    return IndicatorDetailsModel(
        code = "Codigo: $code",
        name = "Nombre: $name",
        unit = "Unidad de Medida: ${unit.toHumanReadable()}",
        date = "Fecha: ${humanReadableDate(date)}",
        value = "Valor: $value"
    )
}
