package dev.josecaldera.indicators.args

import android.os.Parcelable
import dev.josecaldera.indicators.main.domain.model.Indicator
import kotlinx.parcelize.Parcelize

@Parcelize
data class IndicatorArg(
    val code: String,
    val name: String,
    val unit: Indicator.UnitType,
    val date: String,
    val value: String
) : Parcelable

fun Indicator.toParcelable(): IndicatorArg {
    return with(this) {
        IndicatorArg(
            code = code,
            name = name,
            unit = unit,
            date = date,
            value = value
        )
    }
}

fun IndicatorArg.toDomain(): Indicator {
    return with(this) {
        Indicator(
            code = code,
            name = name,
            unit = unit,
            date = date,
            value = value
        )
    }
}
